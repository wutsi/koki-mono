package com.wutsi.koki.platform.ai.agent

import com.wutsi.koki.platform.ai.llm.Config
import com.wutsi.koki.platform.ai.llm.Content
import com.wutsi.koki.platform.ai.llm.Document
import com.wutsi.koki.platform.ai.llm.FunctionCall
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import java.io.File
import java.io.FileInputStream
import java.net.URLConnection

abstract class Agent(
    private val llm: LLM,
    private val maxIterations: Int = 5,
    private val responseType: MediaType = MediaType.TEXT_PLAIN,
) {
    abstract fun systemInstructions(): String?

    abstract fun buildPrompt(query: String, memory: List<String>): String

    abstract fun tools(): List<Tool>

    @Throws(AgentException::class)
    fun run(query: String): String {
        return run(query, emptyList())
    }

    @Throws(AgentException::class)
    fun run(query: String, files: List<File>): String {
        var iteration = 0
        val logger = getLogger()
        val memory: MutableList<String> = mutableListOf()

        while (true) {
            iteration++

            if (logger.isInfoEnabled) {
                logger.info("-----------------------------")
                logger.info("Iteration: #$iteration")
                logger.info("> agent: " + this::class.java.name)
                logger.info("> model: " + llm::class.java.name)
            }
            if (iteration > maxIterations) {
                throw TooManyIterationsException("Too many iteration. iteration=$iteration")
            }

            try {
                if (run(query, memory, files)) {
                    break
                }
            } catch (ex: Exception) {
                throw AgentException("Failure", ex)
            }
        }
        return memory.lastOrNull() ?: ""
    }

    internal fun run(prompt: String, memory: MutableList<String>, files: List<File>): Boolean {
        val response = ask(prompt, files, memory)
        return decide(response, memory)
    }

    private fun decide(
        response: LLMResponse,
        memory: MutableList<String>
    ): Boolean {
        val logger = getLogger()
        var calls = 0

        // Text
        response.messages.flatMap { message -> message.content }
            .forEach { content ->
                if (content.text != null) {
                    if (logger.isInfoEnabled) {
                        logger.info("> ${content.text}")
                    }
                    memory.add(content.text)
                }
            }

        // Function calls
        response.messages.flatMap { message -> message.content }
            .forEach { content ->
                val call = content.functionCall
                if (call != null) {
                    if (logger.isInfoEnabled) {
                        logger.info("> function: " + call.name)
                        logger.info("> args: " + call.args)
                        val result = exec(call)
                        if (result != null) {
                            memory.add(result)
                        }
                        calls++
                    }
                }
            }

        return calls == 0
    }

    private fun exec(call: FunctionCall): String? {
        return tools().find { tool -> tool.function().name == call.name }
            ?.use(call.args)
    }

    private fun ask(query: String, files: List<File>, memory: List<String>): LLMResponse {
        // System instruction
        val messages = mutableListOf<Message>()
        systemInstructions()?.let { instructions ->
            messages.add(
                Message(role = Role.SYSTEM, content = listOf(Content(text = instructions)))
            )
        }

        // prompts
        val inputs = files.associate { file -> file to FileInputStream(file) }
        val prompt = buildPrompt(query, memory)
        getLogger().info("> prompt: $prompt")
        messages.add(
            Message(
                role = Role.USER,
                content = listOf(Content(text = buildPrompt(query, memory))) +
                    inputs.map { input ->
                        val mimeType = URLConnection.guessContentTypeFromName(input.key.path)
                        Content(
                            document = Document(
                                content = input.value,
                                contentType = MediaType.valueOf(mimeType)
                            )
                        )
                    }
            )
        )

        try {
            val request = LLMRequest(
                messages = messages,
                tools = tools().map { tool ->
                    com.wutsi.koki.platform.ai.llm.Tool(
                        functionDeclarations = listOf(tool.function()),
                    )
                },
                config = Config(
                    responseType = responseType
                ),
            )
            return llm.generateContent(request)
        } finally {
            inputs.forEach { input ->
                try {
                    input.value.close()
                } catch (ex: Exception) {
                    // Ignore
                }
            }
        }
    }

    private fun getLogger(): Logger {
        return LoggerFactory.getLogger(this::class.java)
    }
}
