package com.wutsi.koki.platform.ai.agent.react.examples

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.ai.agent.DefaultAgent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.agent.react.ReactAgent
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.FunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.FunctionParameters
import com.wutsi.koki.platform.ai.llm.Type
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import org.apache.commons.io.output.ByteArrayOutputStream

fun main(args: Array<String>) {
    val agent = WeatherAgent()

    // agent.run()
    agent.run2()
}

class WeatherAgent {
    val agent = DefaultAgent(
        llm = Deepseek(
            apiKey = System.getenv("DEEPSEEK_API_KEY"),
            model = "deepseek-chat",
        ),
        tools = listOf(
            WeatherTool()
        ),
        systemInstructions = """
            You are a weather assistant, helping people to know the temperature of cities.
            Instructions:
            - Be very concise in your answers.
        """.trimIndent(),
        maxIterations = 5,
    )

    fun run() {
        val weather = ReactAgent(
            llm = Gemini(
                apiKey = System.getenv("GEMINI_API_KEY"),
                model = "gemini-2.0-flash",
            ),
            agentTools = listOf(
                WeatherTool()
            ),
            query = "Which city is hotter, Yaounde or Montreal?",
            objectMapper = ObjectMapper(),
        )

        weather.think()
    }

    fun run2() {
        val output = ByteArrayOutputStream()
        agent.run(
            query = "Which city is hotter, Yaounde or Montreal?",
            file = null,
            output = output
        )
        println(String(output.toByteArray()))
    }
}

class WeatherTool : Tool {
    override fun function(): FunctionDeclaration {
        return FunctionDeclaration(
            name = "get_weather",
            description = "Get the current real-time weather conditions for a specified city.",
            parameters = FunctionParameters(
                properties = mapOf(
                    "region" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "Region from where we want the weather"
                    ),
                ),
                required = listOf("region")
            )
        )
    }

    override fun use(args: Map<String, Any>): String {
        val temperature = (Math.random() * 100).toInt()
        val input = args["region"]
        return "The temperature in $input is $temperature degrees celcius"
    }
}
