package com.wutsi.koki.platform.ai.agent

import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMConfig
import com.wutsi.koki.platform.ai.llm.LLMContent
import com.wutsi.koki.platform.ai.llm.LLMDocument
import com.wutsi.koki.platform.ai.llm.LLMFunctionCall
import com.wutsi.koki.platform.ai.llm.LLMMessage
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.LLMRole
import com.wutsi.koki.platform.ai.llm.Tool
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
    open fun systemInstructions(): String? = null

    open fun tools(): List<Tool> = emptyList()

    abstract fun buildPrompt(query: String, memory: List<String>): String

    @Throws(AgentException::class)
    fun run(query: String): String {
        return run(query, emptyList())
    }

    @Throws(AgentException::class)
    fun run(query: String, files: List<File>): String {
        var iteration = 0
        val logger = getLogger()
        val memory: MutableList<String> = mutableListOf()
        val start = System.currentTimeMillis()
        while (true) {
            // Increment iteration
            iteration++
            logger.info("llm=${llm::class.java.simpleName} iteration=$iteration")
            if (iteration > maxIterations) {
                throw TooManyIterationsException("Too many iteration. iteration=$iteration")
            }

            // Run
            try {
                if (run(iteration, query, memory, files)) {
                    logger.info("llm=${llm::class.java.simpleName} iteration=$iteration duration=" + duration(start) + "s success=true")
                    break
                }
            } catch (ex: Exception) {
                throw AgentException("Failure", ex)
            }
        }
        return memory.lastOrNull() ?: ""
    }

    internal fun run(iteration: Int, prompt: String, memory: MutableList<String>, files: List<File>): Boolean {
        val response = ask(iteration, prompt, files, memory)
        return decide(iteration, response, memory)
    }

    private fun duration(start: Long): Long {
        return (System.currentTimeMillis() - start) / 1000
    }

    private fun decide(
        iteration: Int,
        response: LLMResponse,
        memory: MutableList<String>
    ): Boolean {
        val logger = getLogger()
        var calls = 0

        // Text
        response.messages.flatMap { message -> message.content }
            .forEach { content ->
                if (!content.text.isNullOrEmpty()) {
                    if (logger.isInfoEnabled()) {
                        logger.debug("llm=${llm::class.java.simpleName} iteration=$iteration step=decide text\n${content.text}")
                    }
                    memory.add(content.text)
                }
            }

        // Function calls
        response.messages.flatMap { message -> message.content }
            .forEach { content ->
                val call = content.functionCall
                if (call != null) {
                    logger.debug("llm=${llm::class.java.simpleName} iteration=$iteration step=decide function=${call.name} args=${call.args}")
                    val result = exec(call)
                    logger.info(result)

                    if (result != null) {
                        memory.add(result)
                    }
                    calls++
                }
            }

        return calls == 0
    }

    private fun exec(call: LLMFunctionCall): String? {
        return tools().find { tool -> tool.function().name == call.name }
            ?.use(call.args)
    }

    private fun ask(iteration: Int, query: String, files: List<File>, memory: List<String>): LLMResponse {
        // System instruction
        val messages = mutableListOf<LLMMessage>()
        systemInstructions()?.let { instructions ->
            messages.add(
                LLMMessage(role = LLMRole.SYSTEM, content = listOf(LLMContent(text = instructions)))
            )
        }

        // prompts
        val inputs = files.associate { file -> file to FileInputStream(file) }
        val prompt = buildPrompt(query, memory)
        if (getLogger().isDebugEnabled) {
            getLogger().debug("llm=${llm::class.java.simpleName} iteration=$iteration step=ask prompt: $prompt")
        }
        messages.add(
            LLMMessage(
                role = LLMRole.USER,
                content = listOf(LLMContent(text = buildPrompt(query, memory))) +
                    inputs.map { input ->
                        val mimeType = URLConnection.guessContentTypeFromName(input.key.path)
                        LLMContent(
                            document = LLMDocument(
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
                    com.wutsi.koki.platform.ai.llm.LLMTool(
                        functionDeclarations = listOf(tool.function()),
                    )
                },
                config = LLMConfig(
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
