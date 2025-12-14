package com.wutsi.koki.platform.ai.agent

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.Content
import com.wutsi.koki.platform.ai.llm.FunctionCall
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import org.mockito.Mockito.mock
import org.springframework.http.MediaType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AgentTest {
    private val llm = mock<LLM>()
    private val tool = mock<Tool>()

    @Test
    fun `returns text response from LLM`() {
        val agent = createAgent(maxIterations = 5)
        val textContent = Content(text = "Hello, this is the response")
        val response = LLMResponse(messages = listOf(Message(role = Role.MODEL, content = listOf(textContent))))
        doReturn(response).whenever(llm).generateContent(any())

        val result = agent.run("What is the weather?")

        assertEquals("Hello, this is the response", result)
    }

    @Test
    fun `returns empty string when no response`() {
        val agent = createAgent(maxIterations = 5)
        val response = LLMResponse(messages = emptyList())
        doReturn(response).whenever(llm).generateContent(any())

        val result = agent.run("What is the weather?")

        assertEquals("", result)
    }

    @Test
    fun `throws TooManyIterationsException when max iterations exceeded`() {
        val agent = createAgent(maxIterations = 2)
        val functionCall = FunctionCall(name = "get_weather", args = mapOf("city" to "Paris"))
        val response = LLMResponse(
            messages = listOf(Message(role = Role.MODEL, content = listOf(Content(functionCall = functionCall))))
        )
        doReturn(response).whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("get_weather", "Gets weather", null)).whenever(tool).function()
        doReturn("Sunny").whenever(tool).use(any())

        assertFailsWith<TooManyIterationsException> {
            agent.run("What is the weather in Paris?")
        }
    }

    @Test
    fun `throws AgentException when LLM fails`() {
        val agent = createAgent(maxIterations = 5)
        doThrow(RuntimeException("LLM error")).whenever(llm).generateContent(any())

        val exception = assertFailsWith<AgentException> {
            agent.run("Query")
        }

        assertTrue(exception.cause is RuntimeException)
    }

    @Test
    fun `executes tool function call and continues iteration`() {
        val agent = createAgent(maxIterations = 5)
        val functionCall = FunctionCall(name = "get_weather", args = mapOf("city" to "Paris"))
        val functionResponse = LLMResponse(
            messages = listOf(Message(role = Role.MODEL, content = listOf(Content(functionCall = functionCall))))
        )
        val textResponse = LLMResponse(
            messages = listOf(Message(role = Role.MODEL, content = listOf(Content(text = "Weather is sunny in Paris"))))
        )
        doReturn(FunctionDeclaration("get_weather", "Gets weather", null)).whenever(tool).function()
        doReturn("Sunny, 25°C").whenever(tool).use(any())
        doReturn(functionResponse).doReturn(textResponse).whenever(llm).generateContent(any())

        val result = agent.run("What is the weather in Paris?")

        assertEquals("Weather is sunny in Paris", result)
        verify(tool).use(mapOf("city" to "Paris"))
    }

    @Test
    fun `includes system instructions in request`() {
        val agent = createAgent(maxIterations = 5, systemInstructions = "You are a helpful assistant")
        val response =
            LLMResponse(messages = listOf(Message(role = Role.MODEL, content = listOf(Content(text = "OK")))))
        doReturn(response).whenever(llm).generateContent(any())

        agent.run("Hello")

        val captor = argumentCaptor<LLMRequest>()
        verify(llm).generateContent(captor.capture())
        val request = captor.firstValue
        assertEquals(2, request.messages.size)
        assertEquals(Role.SYSTEM, request.messages[0].role)
        assertEquals("You are a helpful assistant", request.messages[0].content[0].text)
    }

    @Test
    fun `does not include system instructions when null`() {
        val agent = createAgent(maxIterations = 5, systemInstructions = null)
        val response =
            LLMResponse(messages = listOf(Message(role = Role.MODEL, content = listOf(Content(text = "OK")))))
        doReturn(response).whenever(llm).generateContent(any())

        agent.run("Hello")

        val captor = argumentCaptor<LLMRequest>()
        verify(llm).generateContent(captor.capture())
        val request = captor.firstValue
        assertEquals(1, request.messages.size)
        assertEquals(Role.USER, request.messages[0].role)
    }

    @Test
    fun `handles multiple text responses in single message`() {
        val agent = createAgent(maxIterations = 5)
        val response = LLMResponse(
            messages = listOf(
                Message(
                    role = Role.MODEL,
                    content = listOf(
                        Content(text = "First response"),
                        Content(text = "Second response")
                    )
                )
            )
        )
        doReturn(response).whenever(llm).generateContent(any())

        val result = agent.run("Query")

        assertEquals("Second response", result)
    }

    @Test
    fun `tool not found does not execute`() {
        val agent = createAgent(maxIterations = 5)
        val functionCall = FunctionCall(name = "unknown_function", args = emptyMap())
        val functionResponse = LLMResponse(
            messages = listOf(Message(role = Role.MODEL, content = listOf(Content(functionCall = functionCall))))
        )
        val textResponse = LLMResponse(
            messages = listOf(Message(role = Role.MODEL, content = listOf(Content(text = "Final response"))))
        )
        doReturn(FunctionDeclaration("get_weather", "Gets weather", null)).whenever(tool).function()
        doReturn(functionResponse).doReturn(textResponse).whenever(llm).generateContent(any())

        val result = agent.run("Query")

        assertEquals("Final response", result)
    }

    private fun createAgent(
        maxIterations: Int,
        systemInstructions: String? = null
    ): Agent {
        return object : Agent(llm, maxIterations, MediaType.TEXT_PLAIN) {
            override fun systemInstructions(): String? = systemInstructions

            override fun buildPrompt(query: String, memory: List<String>): String {
                return if (memory.isEmpty()) query else "$query\n\nContext: ${memory.joinToString("\n")}"
            }

            override fun tools(): List<Tool> = listOf(tool)
        }
    }
}
