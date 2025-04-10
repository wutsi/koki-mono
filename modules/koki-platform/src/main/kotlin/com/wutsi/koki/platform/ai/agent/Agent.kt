package com.wutsi.koki.platform.ai.agent

import com.wutsi.koki.platform.ai.llm.Config
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
import kotlin.jvm.Throws

abstract class Agent(
    private val llm: LLM,
    private val maxIterations: Int = 5,
    private val responseType: MediaType = MediaType.TEXT_PLAIN,
) {
    abstract fun systemInstructions(): String?

    abstract fun buildPrompt(query: String, memory: List<String>): String

    abstract fun tools(): List<Tool>

    @Throws(AgentException::class)
    fun run(query: String, file: File? = null): String {
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
                if (run(query, memory, file)) {
                    break
                }
            } catch (ex: Exception) {
                throw AgentException("Failure", ex)
            }
        }
        return memory.lastOrNull() ?: ""
    }

    internal fun run(prompt: String, memory: MutableList<String>, file: File? = null): Boolean {
        val response = ask(prompt, file, memory)
        return decide(response, memory)
    }

    private fun decide(
        response: LLMResponse,
        memory: MutableList<String>
    ): Boolean {
        val logger = getLogger()

        // Text
        response.messages.mapNotNull { message -> message.text }
            .forEach { text ->
                if (logger.isInfoEnabled) {
                    logger.info("> $text")
                }
                memory.add(text)
            }

        // Function calls
        var calls = 0
        response.messages.mapNotNull { message -> message.functionCall }
            .forEach { call ->
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

        return calls == 0
    }

    private fun exec(call: FunctionCall): String? {
        return tools().find { tool -> tool.function().name == call.name }
            ?.use(call.args)
    }

    private fun ask(query: String, file: File?, memory: List<String>): LLMResponse {
        val input = file?.let { FileInputStream(file) }
        try {
            val prompt = buildPrompt(query, memory)
            getLogger().info("> prompt: $prompt")
            val request = LLMRequest(
                messages = listOf(
                    systemInstructions()?.let { instructions -> Message(role = Role.SYSTEM, text = instructions) },
                    Message(role = Role.USER, text = prompt),
                    input?.let {
                        val mimeType = URLConnection.guessContentTypeFromName(file.path)
                        Message(
                            role = Role.USER,
                            document = Document(
                                content = input,
                                contentType = MediaType.valueOf(mimeType)
                            )
                        )
                    }
                ).filterNotNull(),
                tools = tools().map { tool ->
                    com.wutsi.koki.platform.ai.llm.Tool(
                        functionDeclarations = listOf(tool.function()),
                    )
                },
                config = Config(
                    responseType = responseType
                )
            )
            return llm.generateContent(request)
        } finally {
            input?.close()
        }
    }

    private fun getLogger(): Logger {
        return LoggerFactory.getLogger(this::class.java)
    }
}
