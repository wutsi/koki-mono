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
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultAgentTest {
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
        val output = ByteArrayOutputStream()
        val inputs = mapOf("email" to "ray.sponsible@gmail.com")
        setupFunctionCallResponse("1st step", function1.name, inputs)

        // THEN
        val agent = createAgent(responseType = MediaType.APPLICATION_JSON)
        val result = agent.step(QUERY, null, output, memory)

        // Continue
        assertEquals(false, result)

        // LLM is called
        val request = argumentCaptor<LLMRequest>()
        verify(llm).generateContent(request.capture())

        assertEquals(MediaType.APPLICATION_JSON, request.firstValue.config?.responseType)

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
        assertEquals(1, memory.size)
        assertEquals(true, memory.contains(FUNCTION1_RESULT))

        // No Result
        assertEquals("", String(output.toByteArray()))
    }

    @Test
    fun step1() {
        val output = ByteArrayOutputStream()
        val inputs = mapOf("email" to "ray.sponsible@gmail.com")
        setupFunctionCallResponse("1st step", function1.name, inputs)

        val file = File.createTempFile("foo", ".txt")
        file.writeText("Yo man")

        // THEN
        val agent = createAgent(responseType = MediaType.APPLICATION_JSON)
        agent.step(QUERY, null, output, memory)
        val result = agent.step(QUERY, file, output, memory)

        // Continue
        assertEquals(false, result)

        // LLM Called
        val request = argumentCaptor<LLMRequest>()
        verify(llm, times(2)).generateContent(request.capture())

        assertEquals(MediaType.APPLICATION_JSON, request.secondValue.config?.responseType)

        assertEquals(2, request.secondValue.tools?.size)
        assertEquals(function1.name, request.secondValue.tools?.get(0)?.functionDeclarations?.get(0)?.name)
        assertEquals(function1.parameters, request.secondValue.tools?.get(0)?.functionDeclarations?.get(0)?.parameters)
        assertEquals(function2.name, request.secondValue.tools?.get(1)?.functionDeclarations?.get(0)?.name)
        assertEquals(function2.parameters, request.secondValue.tools?.get(1)?.functionDeclarations?.get(0)?.parameters)

        assertEquals(4, request.secondValue.messages.size)
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

        assertEquals(Role.USER, request.secondValue.messages[3].role)
        assertEquals(FUNCTION1_RESULT, request.secondValue.messages[3].text)
        assertEquals(null, request.secondValue.messages[3].document)

        // function is called
        verify(tool1, times(2)).use(inputs)

        // result is added into the memory
        assertEquals(2, memory.size)
        assertEquals(true, memory.contains(FUNCTION1_RESULT))
        assertEquals(true, memory.contains(FUNCTION1_RESULT))

        // No Result
        assertEquals("", String(output.toByteArray()))
    }

    @Test
    fun finalText() {
        val output = ByteArrayOutputStream()
        val inputs = mapOf("email" to "ray.sponsible@gmail.com")

        // THEN
        val agent = createAgent(responseType = MediaType.TEXT_PLAIN)

        setupFunctionCallResponse("1st step", function1.name, inputs)
        agent.step(QUERY, null, output, memory)

        setupFunctionCallResponse("2nd step", function2.name, emptyMap())
        agent.step(QUERY, null, output, memory)

        setupTextResponse("Done")
        val result = agent.step(QUERY, null, output, memory)

        // Continue
        assertEquals(true, result)

        // LLM Called
        val request = argumentCaptor<LLMRequest>()
        verify(llm, times(3)).generateContent(request.capture())

        assertEquals(MediaType.TEXT_PLAIN, request.thirdValue.config?.responseType)

        assertEquals(2, request.thirdValue.tools?.size)
        assertEquals(function1.name, request.thirdValue.tools?.get(0)?.functionDeclarations?.get(0)?.name)
        assertEquals(function1.parameters, request.thirdValue.tools?.get(0)?.functionDeclarations?.get(0)?.parameters)
        assertEquals(function2.name, request.thirdValue.tools?.get(1)?.functionDeclarations?.get(0)?.name)
        assertEquals(function2.parameters, request.thirdValue.tools?.get(1)?.functionDeclarations?.get(0)?.parameters)

        assertEquals(4, request.thirdValue.messages.size)
        assertEquals(Role.SYSTEM, request.thirdValue.messages[0].role)
        assertEquals(SYSTEM_INSTRUCTIONS, request.thirdValue.messages[0].text)
        assertEquals(null, request.thirdValue.messages[0].document)

        assertEquals(Role.USER, request.thirdValue.messages[1].role)
        assertEquals(true, request.thirdValue.messages[1].text?.contains(QUERY))
        assertEquals(null, request.thirdValue.messages[1].document)

        assertEquals(FUNCTION1_RESULT, request.thirdValue.messages[2].text)
        assertEquals(Role.USER, request.thirdValue.messages[2].role)
        assertEquals(null, request.thirdValue.messages[2].document)

        assertEquals(FUNCTION2_RESULT, request.thirdValue.messages[3].text)
        assertEquals(Role.USER, request.thirdValue.messages[3].role)
        assertEquals(null, request.thirdValue.messages[3].document)

        // function is called
        verify(tool1).use(inputs)
        verify(tool2).use(emptyMap())

        // result is added into the memory
        assertEquals(2, memory.size)
        assertEquals(true, memory.contains(FUNCTION1_RESULT))
        assertEquals(true, memory.contains(FUNCTION2_RESULT))

        // Result
        assertEquals("Done", String(output.toByteArray()))
    }

    @Test
    fun finalDocument() {
        val output = ByteArrayOutputStream()

        // THEN
        val agent = createAgent(responseType = MediaType.TEXT_PLAIN)

        setupDocumentResponse("Done")
        val result = agent.step(QUERY, null, output, memory)

        // Continue
        assertEquals(true, result)

        // LLM Called
        val request = argumentCaptor<LLMRequest>()
        verify(llm).generateContent(request.capture())

        assertEquals(MediaType.TEXT_PLAIN, request.firstValue.config?.responseType)

        // Result
        assertEquals("Done", String(output.toByteArray()))
    }

    @Test
    fun empty() {
        val output = ByteArrayOutputStream()

        // THEN
        val agent = createAgent(responseType = MediaType.TEXT_PLAIN)

        setupTextResponse(null)
        val result = agent.step(QUERY, null, output, memory)

        // Continue
        assertEquals(false, result)

        // LLM Called
        val request = argumentCaptor<LLMRequest>()
        verify(llm).generateContent(request.capture())

        // Result
        assertEquals("", String(output.toByteArray()))
    }

    @Test
    fun `too many iterations`() {
        val output = ByteArrayOutputStream()
        val inputs = mapOf("email" to "ray.sponsible@gmail.com")
        setupFunctionCallResponse("1st step", function1.name, inputs)

        // THEN
        val agent = createAgent(responseType = MediaType.APPLICATION_JSON, maxIterations = 2)
        assertThrows<TooManyIterationsException> { agent.run(QUERY, null, output) }
    }

    @Test
    fun error() {
        doThrow(LLMException::class).whenever(llm).generateContent(any())

        // THEN
        assertThrows<AgentException> { createAgent().run(QUERY, null, ByteArrayOutputStream()) }
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

    private fun createAgent(
        maxIterations: Int = 5,
        responseType: MediaType? = MediaType.APPLICATION_JSON,
    ): DefaultAgent {
        return DefaultAgent(
            llm = llm,
            tools = listOf(tool1, tool2),
            maxIterations = maxIterations,
            responseType = responseType,
            systemInstructions = SYSTEM_INSTRUCTIONS
        )
    }
}
