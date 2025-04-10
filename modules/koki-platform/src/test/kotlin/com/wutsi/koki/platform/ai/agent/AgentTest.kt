package com.wutsi.koki.platform.ai.agent

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.Document
import com.wutsi.koki.platform.ai.llm.FunctionCall
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.FunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.FunctionParameters
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMException
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import com.wutsi.koki.platform.ai.llm.Type
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.springframework.http.MediaType
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AgentTest {
    companion object {
        const val SYSTEM_INSTRUCTIONS = "I'm a travel agent who help people to live memorable experience by travelling"
        const val QUERY = "Inspire me for vacation under the sun"
        const val FUNCTION1_RESULT = "This is the result of function1"
        const val FUNCTION2_RESULT = "This is the result of function2"
    }

    private val function1 = FunctionDeclaration(
        name = "tool1",
        description = "This is the description of tool1",
        parameters = FunctionParameters(
            properties = mapOf("email" to FunctionParameterProperty(type = Type.STRING, "Email"))
        ),
    )
    private val function2 = FunctionDeclaration(
        name = "tool2",
        description = "This is the description of tool2",
        parameters = null,
    )

    private val tool1 = mock<Tool>()
    private val tool2 = mock<Tool>()
    private val llm = mock<LLM>()
    private val memory = mutableListOf<String>()

    @BeforeTest
    fun setUp() {
        doReturn(function1).whenever(tool1).function()
        doReturn(FUNCTION1_RESULT).whenever(tool1).use(any())

        doReturn(function2).whenever(tool2).function()
        doReturn(FUNCTION2_RESULT).whenever(tool2).use(any())

        memory.clear()
    }

    @Test
    fun step0() {
        val inputs = mapOf("email" to "ray.sponsible@gmail.com")
        setupFunctionCallResponse("1st step", function1.name, inputs)

        // THEN
        val agent = createAgent()
        val result = agent.run(QUERY, memory)

        // Continue
        assertEquals(false, result)

        // LLM is called
        val request = argumentCaptor<LLMRequest>()
        verify(llm).generateContent(request.capture())

        assertEquals(2, request.firstValue.tools?.size)
        assertEquals(function1.name, request.firstValue.tools?.get(0)?.functionDeclarations?.get(0)?.name)
        assertEquals(function1.parameters, request.firstValue.tools?.get(0)?.functionDeclarations?.get(0)?.parameters)
        assertEquals(function2.name, request.firstValue.tools?.get(1)?.functionDeclarations?.get(0)?.name)
        assertEquals(function2.parameters, request.firstValue.tools?.get(1)?.functionDeclarations?.get(0)?.parameters)

        assertEquals(2, request.firstValue.messages.size)
        assertEquals(Role.SYSTEM, request.firstValue.messages[0].role)
        assertEquals(SYSTEM_INSTRUCTIONS, request.firstValue.messages[0].text)
        assertEquals(null, request.firstValue.messages[0].document)

        assertEquals(Role.USER, request.firstValue.messages[1].role)
        assertEquals(true, request.firstValue.messages[1].text?.contains(QUERY))
        assertEquals(null, request.firstValue.messages[1].document)

        // function is called
        verify(tool1).use(inputs)

        // result is added into the memory
        assertEquals(2, memory.size)
        assertEquals(listOf("1st step", FUNCTION1_RESULT), memory)
    }

    @Test
    fun step1() {
        val inputs = mapOf("email" to "ray.sponsible@gmail.com")

        val file = File.createTempFile("foo", ".txt")
        file.writeText("Yo man")

        // THEN
        val agent = createAgent()
        setupFunctionCallResponse("1st step", function1.name, inputs)
        agent.run(QUERY, memory)

        setupFunctionCallResponse("2nd step", function2.name, emptyMap())
        val result = agent.run(QUERY, memory, file)

        // Continue
        assertEquals(false, result)

        // LLM Called
        val request = argumentCaptor<LLMRequest>()
        verify(llm, times(2)).generateContent(request.capture())

        assertEquals(3, request.secondValue.messages.size)
        assertEquals(Role.SYSTEM, request.secondValue.messages[0].role)
        assertEquals(SYSTEM_INSTRUCTIONS, request.secondValue.messages[0].text)
        assertEquals(null, request.secondValue.messages[0].document)

        assertEquals(Role.USER, request.secondValue.messages[1].role)
        assertEquals(true, request.secondValue.messages[1].text?.contains(QUERY))
        assertEquals(null, request.secondValue.messages[1].document)

        assertEquals(Role.USER, request.secondValue.messages[2].role)
        assertEquals(null, request.secondValue.messages[2].text)
        assertEquals(MediaType.TEXT_PLAIN, request.secondValue.messages[2].document?.contentType)
        assertNotNull(request.secondValue.messages[2].document?.content)

        // function is called
        verify(tool1).use(any())
        verify(tool2).use(any())

        // result is added into the memory
        assertEquals(
            listOf("1st step", FUNCTION1_RESULT, "2nd step", FUNCTION2_RESULT),
            memory,
        )
    }

    @Test
    fun finalText() {
        val inputs = mapOf("email" to "ray.sponsible@gmail.com")

        // THEN
        val agent = createAgent()

        setupFunctionCallResponse("1st step", function1.name, inputs)
        agent.run(QUERY, memory)

        setupFunctionCallResponse("2nd step", function2.name, emptyMap())
        agent.run(QUERY, memory)

        setupTextResponse("Done")
        val result = agent.run(QUERY)

        // Continue
        assertEquals("Done", result)
    }

    @Test
    fun `too many iterations`() {
        val inputs = mapOf("email" to "ray.sponsible@gmail.com")
        setupFunctionCallResponse("1st step", function1.name, inputs)

        // THEN
        val agent = createAgent(maxIterations = 2)
        assertThrows<TooManyIterationsException> { agent.run(QUERY, null) }
    }

    @Test
    fun error() {
        doThrow(LLMException::class).whenever(llm).generateContent(any())

        // THEN
        assertThrows<AgentException> { createAgent().run(QUERY) }
    }

    private fun setupFunctionCallResponse(
        thought: String,
        function: String,
        args: Map<String, String> = emptyMap()
    ) {
        doReturn(
            LLMResponse(
                messages = listOf(
                    Message(text = thought, role = Role.MODEL),
                    Message(
                        text = null,
                        role = Role.MODEL,
                        functionCall = FunctionCall(
                            name = function,
                            args = args,
                        )
                    ),
                ),
            )
        ).whenever(llm).generateContent(any())
    }

    private fun setupTextResponse(text: String?) {
        doReturn(
            LLMResponse(
                messages = listOf(
                    Message(text = text, role = Role.MODEL),
                ),
            )
        ).whenever(llm).generateContent(any())
    }

    private fun setupDocumentResponse(text: String) {
        doReturn(
            LLMResponse(
                messages = listOf(
                    Message(
                        document = Document(
                            contentType = MediaType.TEXT_PLAIN,
                            content = ByteArrayInputStream(text.toByteArray()),
                        ),
                        role = Role.MODEL
                    ),
                ),
            )
        ).whenever(llm).generateContent(any())
    }

    private fun createAgent(maxIterations: Int = 5): Agent {
        return AgentImpl(
            llm = llm,
            maxIterations = maxIterations,
            systemInstructions = SYSTEM_INSTRUCTIONS,
            tools = listOf(tool1, tool2)
        )
    }
}

class AgentImpl(
    val systemInstructions: String,
    val tools: List<Tool>,
    llm: LLM,
    maxIterations: Int
) : Agent(llm, maxIterations) {
    override fun systemInstructions() = systemInstructions

    override fun buildPrompt(query: String, memory: List<String>) =
        "Query: $query\n\n" +
            "Observations:\n" + memory.map { "- $it" }.joinToString("\n")

    override fun tools() = tools
}
