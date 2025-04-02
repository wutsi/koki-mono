package com.wutsi.koki.platform.ai.agent.react

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.http.MediaType
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentTest {
    private val function1 = FunctionDeclaration(
        name = "tool1",
        description = "This is the description of tool1",
        parameters = null,
    )
    private val function2 = FunctionDeclaration(
        name = "tool2",
        description = "This is the description of tool2",
        parameters = null,
    )

    private val tool1 = mock<AgentTool>()
    private val tool2 = mock<AgentTool>()
    private val llm = mock<LLM>()
    private val memory: MutableList<String> = mutableListOf()
    private val template: String = """
        This is the prompt template.
        Query:
        {{query}}

        History:
        {{history}}

        Available Tools:
        {{tools}}
    """.trimIndent()
    private val query = "This is the query"

    @BeforeEach
    fun setUp() {
        doReturn(function1).whenever(tool1).function()
        doReturn(function2).whenever(tool2).function()

        memory.clear()
    }

    @Test
    fun `final thought`() {
        // GIVEN
        setupLLMResponse(
            """
            {
                "thought": "This is the final response",
                "answer": "FINISH LINE!!!"
            }
            """.trimIndent(),
        )

        // WHEN
        createAgent(query).think()

        // VERIFY
        val request = argumentCaptor<LLMRequest>()
        verify(llm).generateContent(request.capture())
        assertEquals(
            """
                This is the prompt template.
                Query:
                $query

                History:


                Available Tools:
                - ${function1.name}: ${function1.description}
                - ${function2.name}: ${function2.description}
            """.trimIndent(),
            request.firstValue.messages[0].text,
        )
        assertEquals(MediaType.APPLICATION_JSON, request.firstValue.config?.responseType)
        assertEquals(2, request.firstValue.tools?.size)
        assertEquals(function1, request.firstValue.tools?.get(0)?.functionDeclarations?.get(0))
        assertEquals(function2, request.firstValue.tools?.get(1)?.functionDeclarations?.get(0))

        assertEquals(0, memory.size)
    }

    @Test
    fun `function call`() {
        // GIVEN
        setupLLMResponse(
            text1 = """
            {
                "thought": "Your detailed reasoning about what to do next",
                "action": {
                    "name": "${function1.name}",
                    "reason": "Explanation of why you chose this tool",
                    "inputs": {
                        "arg1": "value1",
                        "arg2": "value2"
                    }
                }
            }
            """.trimIndent(),
            text2 = """
            {
                "thought": "This is the final response",
                "answer": "FINISH LINE!!!"
            }
            """.trimIndent(),
        )

        doReturn("Result of tool1").whenever(tool1).use(any())

        // WHEN
        createAgent(query).think()

        // VERIFY
        val request = argumentCaptor<LLMRequest>()
        verify(llm, times(2)).generateContent(request.capture())
        assertEquals(
            """
                This is the prompt template.
                Query:
                $query

                History:


                Available Tools:
                - ${function1.name}: ${function1.description}
                - ${function2.name}: ${function2.description}
            """.trimIndent(),
            request.firstValue.messages[0].text,
        )
        assertEquals(
            """
                This is the prompt template.
                Query:
                $query

                History:
                - Observation from tool1: Result of tool1

                Available Tools:
                - ${function1.name}: ${function1.description}
                - ${function2.name}: ${function2.description}
            """.trimIndent(),
            request.secondValue.messages[0].text,
        )

        assertEquals(1, memory.size)
        assertEquals("Observation from tool1: Result of tool1", memory[0])

        val args = argumentCaptor<Map<String, Any>>()
        verify(tool1).use(args.capture())
        assertEquals("value1", args.firstValue.get("arg1"))
        assertEquals("value2", args.firstValue.get("arg2"))
    }

    @Test
    fun `function call error`() {
        // GIVEN
        setupLLMResponse(
            text1 = """
            {
                "thought": "Your detailed reasoning about what to do next",
                "action": {
                    "name": "${function1.name}",
                    "reason": "Explanation of why you chose this tool",
                    "inputs": {
                        "arg1": "value1",
                        "arg2": "value2"
                    }
                }
            }
            """.trimIndent(),
            text2 = """
            {
                "thought": "This is the final response",
                "answer": "FINISH LINE!!!"
            }
            """.trimIndent(),
        )

        doThrow(RuntimeException("Unable to fetch data")).whenever(tool1).use(any())

        // WHEN
        createAgent(query).think()

        // VERIFY
        val request = argumentCaptor<LLMRequest>()
        verify(llm, times(2)).generateContent(request.capture())
        assertEquals(
            """
                This is the prompt template.
                Query:
                $query

                History:


                Available Tools:
                - ${function1.name}: ${function1.description}
                - ${function2.name}: ${function2.description}
            """.trimIndent(),
            request.firstValue.messages[0].text,
        )
        assertEquals(
            """
                This is the prompt template.
                Query:
                $query

                History:
                - Unable to use the tool tool1. The error message is: Unable to fetch data

                Available Tools:
                - ${function1.name}: ${function1.description}
                - ${function2.name}: ${function2.description}
            """.trimIndent(),
            request.secondValue.messages[0].text,
        )

        assertEquals(1, memory.size)
        assertEquals("Unable to use the tool tool1. The error message is: Unable to fetch data", memory[0])

        val args = argumentCaptor<Map<String, Any>>()
        verify(tool1).use(args.capture())
        assertEquals("value1", args.firstValue.get("arg1"))
        assertEquals("value2", args.firstValue.get("arg2"))
    }

    @Test
    fun `max iterations`() {
        // GIVEN
        setupLLMResponse(
            """
            {
                "thought": "Your detailed reasoning about what to do next",
                "action": {
                    "name": "${function1.name}",
                    "reason": "Explanation of why you chose this tool",
                    "inputs": {
                        "arg1": "value1",
                        "arg2": "value2"
                    }
                }
            }
            """.trimIndent(),
        )

        doReturn("Result of tool1").whenever(tool1).use(any())

        // WHEN
        createAgent(query, maxIterations = 5).think()

        // VERIFY
        val request = argumentCaptor<LLMRequest>()
        verify(llm, times(5)).generateContent(request.capture())

        assertEquals(5, memory.size)
        assertEquals("Observation from tool1: Result of tool1", memory[0])
        assertEquals("Observation from tool1: Result of tool1", memory[1])
        assertEquals("Observation from tool1: Result of tool1", memory[2])
        assertEquals("Observation from tool1: Result of tool1", memory[3])
        assertEquals("Observation from tool1: Result of tool1", memory[4])

        val args = argumentCaptor<Map<String, Any>>()
        verify(tool1, times(5)).use(args.capture())
    }

    private fun setupLLMResponse(text: String) {
        doReturn(
            LLMResponse(
                messages = listOf(Message(text = text))
            )
        ).whenever(llm).generateContent(any())
    }

    private fun setupLLMResponse(text1: String, text2: String) {
        doReturn(
            LLMResponse(
                messages = listOf(Message(text = text1))
            )
        )
            .doReturn(
                LLMResponse(
                    messages = listOf(Message(text = text2))
                )
            )
            .whenever(llm).generateContent(any())
    }

    private fun createAgent(query: String, maxIterations: Int = 5): Agent {
        return Agent(
            llm = llm,
            agentTools = listOf(tool1, tool2),
            objectMapper = ObjectMapper(),
            query = query,
            maxIterations = maxIterations,
            memory = memory,
            template = template,
        )
    }
}
