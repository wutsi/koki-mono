package com.wutsi.koki.platform.ai.agent.react

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.FunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.FunctionParameters
import com.wutsi.koki.platform.ai.llm.Type
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import org.springframework.web.client.RestTemplate
import kotlin.test.Test

class WeatherAgentTest {
    @Test
    fun run() {
        val weather = Agent(
            llm = Gemini(
                apiKey = System.getenv("GEMINI_API_KEY"),
                model = "gemini-2.0-flash",
                rest = RestTemplate(),
            ),
            agentTools = listOf(
                WeatherTool()
            ),
            query = "Which city is hotter, Yaounde or Montreal?",
            objectMapper = ObjectMapper(),
        )

        weather.think()
    }
}

class WeatherTool : AgentTool {
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
