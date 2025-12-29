package com.wutsi.koki.platform.ai.agent.react

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMConfig
import com.wutsi.koki.platform.ai.llm.LLMContent
import com.wutsi.koki.platform.ai.llm.LLMMessage
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMRole
import com.wutsi.koki.platform.ai.llm.Tool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import tools.jackson.databind.json.JsonMapper

/**
 * Implementation of <a href="https://medium.com/google-cloud/building-react-agents-from-scratch-a-hands-on-guide-using-gemini-ffe4621d90ae">ReAct</a> agents
 */
class ReactAgent(
    private val llm: LLM,
    private val query: String,
    private val agentTools: List<Tool>,
    private val jsonMapper: JsonMapper,
    private val memory: MutableList<String> = mutableListOf(),
    private val template: String = TEMPLATE,
    private val maxIterations: Int = 5,
) {
    companion object {
        const val TEMPLATE = """
            You are a ReAct (Reasoning and Acting) agent tasked with answering the following query:

            Query: {{query}}

            Your goal is to reason about the query and decide on the best course of action to answer it accurately.

            Previous reasoning steps and observations:
            {{history}}

            Available tools:
            {{tools}}

            Instructions:
            1. Analyze the query, previous reasoning steps, and observations.
            2. Decide on the next action: use a tool or provide a final answer.
            3. Respond in the following JSON format:

            If you need to use a tool:
            {{
                "thought": "Your detailed reasoning about what to do next",
                "action": {{
                    "name": "LLMTool name, excluding the namespace (wikipedia, google, or none)",
                    "reason": "Explanation of why you chose this tool",
                    "inputs": "Specific inputs for the tool, as array of key/pair values"
                }}
            }}

            If you have enough information to answer the query:
            {{
                "thought": "Your final reasoning process",
                "answer": "Your comprehensive answer to the query"
            }}

            Remember:
            - Be thorough in your reasoning.
            - Use tools when you need more information.
            - Always base your reasoning on the actual observations from tool use.
            - If a tool returns no results or fails, acknowledge this and consider using a different tool or approach.
            - Provide a final answer only when you're confident you have sufficient information.
            - If you cannot find the necessary information after using available tools, admit that you don't have enough information to answer the query confidently.
        """
    }

    private var iteration: Int = 0
    private val toolMap = agentTools.associateBy { tool -> tool.function().name }.toMap()

    fun think() {
        if (++iteration > maxIterations) {
            return
        }

        try {
            val prompt = buildPrompt()
            val response = ask(prompt)
            trace(LLMRole.MODEL, "Thought: $response")
            decide(response)
        } catch (ex: Exception) {
            getLogger().warn("An unexpected error has occured", ex)
            trace(LLMRole.SYSTEM, "An unexpected error has occured. The error message is: ${ex.message}")
            think()
        }
    }

    private fun trace(role: LLMRole, content: String) {
        if (role == LLMRole.SYSTEM) {
            memory.add(content)
        }
    }

    private fun decide(response: String) {
        val logger = getLogger()
        logger.info("--------------------------------------------------------------------")
        logger.info("Iteration: $iteration")
        logger.info("> agent: " + this::class.java.name)
        logger.info("> model: " + llm::class.java.name)

        try {
            val resp: Response = jsonMapper.readValue(response, Response::class.java)
            logger.info("> ${resp.thought}")

            if (resp.answer != null) {
                logger.info("Final answer: ${resp.answer}")

                trace(LLMRole.MODEL, "  Final answer: ${resp.answer}")
            } else if (resp.action != null) {
                logger.info("> action.reason: ${resp.action.reason}")
                logger.info("> action.name: ${resp.action.name}")
                logger.info("> action.inputs: ${resp.action.inputs}")

                act(resp.action)
            } else {
                throw IllegalStateException("Invalid response")
            }
        } catch (ex: Exception) {
            logger.warn("Unable to process the response: $response", ex)
            think()
        }
    }

    private fun act(action: Action) {
        val tool = toolMap[action.name]
        if (tool != null) {
            try {
                val result = tool.use(action.inputs)

//                getLogger().info("result: $result")
                trace(LLMRole.SYSTEM, "Observation from ${action.name}: $result")
            } catch (ex: Exception) {
                getLogger().warn("An unexpected error has occured while using the tool ${action.name}", ex)
                trace(LLMRole.SYSTEM, "Unable to use the tool ${action.name}. The error message is: ${ex.message}")
            }
        } else {
            trace(LLMRole.SYSTEM, "The tool ${action.name} is not available")
        }
        think()
    }

    private fun buildPrompt(): String {
        return template
            .replace("{{query}}", query)
            .replace(
                "{{history}}",
                memory.joinToString(separator = "\n") { observation -> "- $observation" }
            )
            .replace(
                "{{tools}}",
                toolMap.values.joinToString(separator = "\n") { tool -> "- ${tool.function().name}: ${tool.function().description}" }
            )
            .trimIndent()
    }

    private fun ask(prompt: String): String {
        val response = llm.generateContent(
            request = LLMRequest(
                messages = listOf(
                    LLMMessage(
                        content = listOf(LLMContent(text = prompt))
                    )
                ),
                tools = toolMap.values.map { tool ->
                    com.wutsi.koki.platform.ai.llm.LLMTool(
                        functionDeclarations = listOf(tool.function())
                    )
                },
                config = LLMConfig(
                    responseType = MediaType.APPLICATION_JSON,
                )
            )
        )
        return response.messages.firstOrNull()?.content?.firstOrNull()?.text ?: ""
    }

    private fun getLogger(): Logger {
        return LoggerFactory.getLogger(this::class.java)
    }
}
