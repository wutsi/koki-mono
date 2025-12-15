package com.wutsi.koki.platform.ai.agent.react

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.Content
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import org.mockito.Mockito.mock
import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test
import kotlin.test.assertTrue

class ReactAgentTest {
    private val llm = mock<LLM>()
    private val tool = mock<Tool>()
    private val jsonMapper = JsonMapper.builder().build()

    @Test
    fun `stops when final answer is provided`() {
        val response = """{"thought": "I have the answer", "answer": "The answer is 42"}"""
        doReturn(createLLMResponse(response)).whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search tool", null)).whenever(tool).function()

        val agent = createAgent()

        agent.think()

        verify(llm, times(1)).generateContent(any())
    }

    @Test
    fun `executes tool action and continues thinking`() {
        val actionResponse =
            """{"thought": "Need to search", "action": {"name": "search", "reason": "To find data", "inputs": {"query": "test"}}}"""
        val answerResponse = """{"thought": "Got the data", "answer": "Found the result"}"""
        doReturn(createLLMResponse(actionResponse))
            .doReturn(createLLMResponse(answerResponse))
            .whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search tool", null)).whenever(tool).function()
        doReturn("Search result data").whenever(tool).use(any())

        val memory = mutableListOf<String>()
        val agent = createAgent(memory = memory)

        agent.think()

        verify(llm, times(2)).generateContent(any())
        verify(tool).use(mapOf("query" to "test"))
        assertTrue(memory.any { it.contains("Observation from search") })
    }

    @Test
    fun `stops at max iterations`() {
        val actionResponse =
            """{"thought": "Need to search", "action": {"name": "search", "reason": "To find data", "inputs": {}}}"""
        doReturn(createLLMResponse(actionResponse)).whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search tool", null)).whenever(tool).function()
        doReturn("Result").whenever(tool).use(any())

        val agent = createAgent(maxIterations = 3)

        agent.think()

        verify(llm, times(3)).generateContent(any())
    }

    @Test
    fun `handles tool not found`() {
        val actionResponse =
            """{"thought": "Need to search", "action": {"name": "unknown_tool", "reason": "To find data", "inputs": {}}}"""
        val answerResponse = """{"thought": "Tool not available", "answer": "Cannot complete"}"""
        doReturn(createLLMResponse(actionResponse))
            .doReturn(createLLMResponse(answerResponse))
            .whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search tool", null)).whenever(tool).function()

        val memory = mutableListOf<String>()
        val agent = createAgent(memory = memory)

        agent.think()

        verify(tool, never()).use(any())
        assertTrue(memory.any { it.contains("The tool unknown_tool is not available") })
    }

    @Test
    fun `handles tool execution error`() {
        val actionResponse =
            """{"thought": "Need to search", "action": {"name": "search", "reason": "To find data", "inputs": {}}}"""
        val answerResponse = """{"thought": "Error occurred", "answer": "Failed to get result"}"""
        doReturn(createLLMResponse(actionResponse))
            .doReturn(createLLMResponse(answerResponse))
            .whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search tool", null)).whenever(tool).function()
        doThrow(RuntimeException("Tool error")).whenever(tool).use(any())

        val memory = mutableListOf<String>()
        val agent = createAgent(memory = memory)

        agent.think()

        assertTrue(memory.any { it.contains("Unable to use the tool search") })
    }

    @Test
    fun `handles LLM error and retries`() {
        val answerResponse = """{"thought": "Recovered", "answer": "Success"}"""
        doThrow(RuntimeException("LLM error"))
            .doReturn(createLLMResponse(answerResponse))
            .whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search tool", null)).whenever(tool).function()

        val memory = mutableListOf<String>()
        val agent = createAgent(memory = memory)

        agent.think()

        verify(llm, times(2)).generateContent(any())
        assertTrue(memory.any { it.contains("An unexpected error has occured") })
    }

    @Test
    fun `handles invalid JSON response and retries`() {
        val invalidResponse = "not valid json"
        val answerResponse = """{"thought": "Valid now", "answer": "Success"}"""
        doReturn(createLLMResponse(invalidResponse))
            .doReturn(createLLMResponse(answerResponse))
            .whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search tool", null)).whenever(tool).function()

        val agent = createAgent()

        agent.think()

        verify(llm, times(2)).generateContent(any())
    }

    @Test
    fun `handles response with neither answer nor action`() {
        val invalidResponse = """{"thought": "I am thinking"}"""
        val answerResponse = """{"thought": "Now I have answer", "answer": "The answer"}"""
        doReturn(createLLMResponse(invalidResponse))
            .doReturn(createLLMResponse(answerResponse))
            .whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search tool", null)).whenever(tool).function()

        val agent = createAgent()

        agent.think()

        verify(llm, times(2)).generateContent(any())
    }

    @Test
    fun `builds prompt with query and history`() {
        val answerResponse = """{"thought": "Done", "answer": "Result"}"""
        doReturn(createLLMResponse(answerResponse)).whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search for information", null)).whenever(tool).function()

        val memory = mutableListOf("Previous observation 1", "Previous observation 2")
        val agent = createAgent(query = "What is AI?", memory = memory)

        agent.think()

        verify(llm).generateContent(any())
    }

    @Test
    fun `handles empty LLM response`() {
        val emptyResponse = LLMResponse(messages = emptyList())
        val answerResponse = """{"thought": "Retry worked", "answer": "Success"}"""
        doReturn(emptyResponse)
            .doReturn(createLLMResponse(answerResponse))
            .whenever(llm).generateContent(any())
        doReturn(FunctionDeclaration("search", "Search tool", null)).whenever(tool).function()

        val agent = createAgent()

        agent.think()

        verify(llm, times(2)).generateContent(any())
    }

    private fun createLLMResponse(text: String): LLMResponse {
        return LLMResponse(
            messages = listOf(
                Message(
                    role = Role.MODEL,
                    content = listOf(Content(text = text))
                )
            )
        )
    }

    private fun createAgent(
        query: String = "Test query",
        memory: MutableList<String> = mutableListOf(),
        maxIterations: Int = 5
    ): ReactAgent {
        return ReactAgent(
            llm = llm,
            query = query,
            agentTools = listOf(tool),
            jsonMapper = jsonMapper,
            memory = memory,
            maxIterations = maxIterations
        )
    }
}
