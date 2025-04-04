package com.wutsi.koki.platform.ai.agent

import com.wutsi.koki.platform.ai.llm.Config
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import java.io.ByteArrayInputStream
import java.io.OutputStream

class DefaultAgent(
    private val llm: LLM,
    private val tools: List<Tool>,
    private val maxIterations: Int = 5,
    private val systemInstructions: String? = null,
    private val responseType: MediaType? = null,
) : Agent {
    private val toolMap = tools
        .map { tool -> tool.function().name to tool }
        .toMap()

    override fun run(query: String, output: OutputStream) {
        var iteration = 0
        val logger = getLogger()
        val memory: MutableList<String> = mutableListOf()

        while (true) {
            iteration++
            logger.info("-----------------------------")
            logger.info("Iteration: #$iteration")
            logger.info("> agent: " + this::class.java.name)
            logger.info("> model: " + llm::class.java.name)
            if (iteration > maxIterations) {
                throw TooManyIterationsException("Too many iteration. iteration=$iteration")
            }

            try {
                if (step(query, output, memory)) {
                    break
                }
            } catch (ex: Exception) {
                throw AgentException("Failure", ex)
            }
        }
    }

    fun step(query: String, output: OutputStream, memory: MutableList<String>): Boolean {
        val response = ask(query, memory)
        return decide(response, memory, output)
    }

    private fun decide(
        response: LLMResponse,
        memory: MutableList<String>,
        outputStream: OutputStream
    ): Boolean {
        val logger = getLogger()

        // FUNCTION CALLS
        val functions = response.messages.mapNotNull { message -> message.functionCall }
        if (functions.isNotEmpty()) {
            // Reasoning
            response.messages.filter { message -> message.text != null }
                .forEach { message ->
                    logger.info("> thought: ${message.text}")
                }

            // Execution
            functions.forEach { function ->
                logger.info("> function: " + function.name)
                logger.info("> args: " + function.args)
                val result = toolMap[function.name]?.use(function.args)
                if (result != null) {
                    memory.add(result)
                }
            }
        } else {
            val message = response.messages.firstOrNull() ?: return false
            if (message.text != null) {
                logger.info("> FINAL RESULT: ${message.text}")
                IOUtils.copy(ByteArrayInputStream(message.text.toByteArray()), outputStream)
                return true
            } else if (message.document != null) {
                logger.info("> FINAL RESULT: Document<${message.document.contentType}>")
                IOUtils.copy(message.document.content, outputStream)
                return true
            } else {
                logger.warn("> !!No response")
            }
        }
        return false
    }

    private fun ask(query: String, memory: List<String>): LLMResponse {
        val prompt = buildPrompt(query, memory)
//        getLogger().info("> prompt: $prompt")

        val messages = mutableListOf<Message>()
        if (systemInstructions != null) {
            messages.add(Message(role = Role.SYSTEM, text = systemInstructions))
        }
        messages.add(Message(role = Role.USER, text = prompt))
        messages.addAll(
            memory.map { text -> Message(role = Role.USER, text = text) }
        )

        return llm.generateContent(
            request = LLMRequest(
                messages = messages,
                tools = tools.map { tool ->
                    com.wutsi.koki.platform.ai.llm.Tool(
                        functionDeclarations = listOf(tool.function())
                    )
                },
                config = Config(
                    responseType = responseType,
                ),
            )
        )
    }

    private fun buildPrompt(query: String, memory: List<String>): String {
        val tools = toolMap.values.map { tool -> "- ${tool.function().name}: ${tool.function().description}" }
            .joinToString(separator = "\n")

        return """
            Query: {{query}}

            Available Tools:
            {{tools}}

            Instructions
            1. Analyze the query, previous reasoning steps, and observations.
            2. Decide on the next action: use a tool or provide a final answer.

        """.trimIndent()
            .replace("{{query}}", query)
            .replace("{{tools}}", tools)
    }

    private fun getLogger(): Logger {
        return LoggerFactory.getLogger(this::class.java)
    }
}
