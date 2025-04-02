package com.wutsi.koki.platform.ai.agent.react.examples

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.ai.agent.react.Agent
import com.wutsi.koki.platform.ai.agent.react.AgentTool
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.FunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.FunctionParameters
import com.wutsi.koki.platform.ai.llm.Type
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import org.apache.commons.io.IOUtils
import org.springframework.web.client.RestTemplate

fun main(args: Array<String>) {
    val agent = TravelAgent()

    agent.anonymous()
    // agent.herve()
    // agent.poorGuy()
}

class TravelAgent {
    fun anonymous() {
        run(
            """
                Hi!
                My name is Ray Sponsible.
                I want to goto Cancun in June with my wife for a max budget of $5000.
                Can you recommend me some offers?
            """.trimIndent()
        )
    }

    fun herve() {
        run(
            """
                Hi!
                My name is Herve Tchepannou, my email is herve.tchepannou@gmail.com.
                I want to goto Cancun in June with my wife and 3 kids, my budget is $10000.
                Can you recommend me some offers?
            """.trimIndent()
        )
    }

    fun poorGuy() {
        run(
            """
                Hi!
                My name is John Smith.
                I want to goto Cancun in June and my budget is $1500
                Can you recommend me some offers?
            """.trimIndent()
        )
    }

    private fun run(query: String) {
        val agent = Agent(
            llm = Gemini(
                apiKey = System.getenv("GEMINI_API_KEY"),
                model = "gemini-2.0-flash",
                rest = RestTemplate(),
            ),
            agentTools = listOf(
                PackagesTool(),
                ProfileTool(),
                BookingTool(),
                GeolocalizationTool(),
            ),
            query = """
                $query

                If the traveller origin is not provided:
                - When the email is provided, resolve the user's profile to find it's origin.
                - When the email is not provided, use geo-localization service to resolve its origin.
                - If you do not know the traveller's email, never try to assume it for resolving its profile!
                Before making your final recommendation:
                - When the user is identified (user ID known):
                    - you must always fetch the user past bookings to select offers that matches his preferences (Ex: air carrier, hotel, hotel ratings etc.).
                    - When you can't find offer that matches traveller preferences, select offers based on ratings and reviews
                - When the user is not identified, recommend offers based on ratings and number of reviews.
                - Recommend a maximum of 3 offers.
            """.trimIndent(),
            objectMapper = ObjectMapper(),
            maxIterations = 10,
        )

        agent.think()
    }
}

class GeolocalizationTool : AgentTool {
    override fun function(): FunctionDeclaration {
        return FunctionDeclaration(
            name = "resolve_origin",
            description = "Geo localization service for resolving user location",
            parameters = null
        )
    }

    override fun use(args: Map<String, Any>): String {
        return "The location of the traveller is Seattle, Washington"
    }
}

class ProfileTool : AgentTool {
    override fun function(): FunctionDeclaration {
        return FunctionDeclaration(
            name = "get_user_profile",
            description = "Return personal informations about a user (name, email, location, loyalty earnings etc.)",
            parameters = FunctionParameters(
                properties = mapOf(
                    "email" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "Email of the user"
                    ),
                ),
                required = listOf("email")
            )
        )
    }

    override fun use(args: Map<String, Any>): String {
        val dir = "/ai/agent/travel/data/profiles"
        val email = args["email"].toString()
        val input = this::class.java.getResource("$dir/$email.json")
        if (input == null) {
            return "$email doesn't have a user profile"
        } else {
            val json = IOUtils.toString(input, "utf-8")
            return """
            Here is the user profile in JSON format
            ```json
                $json
            ```
        """.trimIndent()
        }
    }
}

class BookingTool : AgentTool {
    override fun function(): FunctionDeclaration {
        return FunctionDeclaration(
            name = "get_user_bookings",
            description = "Return the past bookings of a user",
            parameters = FunctionParameters(
                properties = mapOf(
                    "user_id" to FunctionParameterProperty(
                        type = Type.INTEGER,
                        description = "ID of the user"
                    ),
                ),
                required = listOf("user_id")
            )
        )
    }

    override fun use(args: Map<String, Any>): String {
        val dir = "/ai/agent/travel/data/bookings"
        val userId = args["user_id"].toString()
        val input = this::class.java.getResource("$dir/$userId.json")
        if (input != null) {
            val json = IOUtils.toString(input, "utf-8")
            return """
                Here is the user bookings
                ```json
                    $json
                ```
            """.trimIndent()
        } else {
            return "No past booking found for $input"
        }
    }
}

class PackagesTool : AgentTool {
    override fun function(): FunctionDeclaration {
        return FunctionDeclaration(
            name = "search_packages",
            description = "Return a list of packages (Hotel and Flights)",
            parameters = FunctionParameters(
                properties = mapOf(
                    "origin" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "3 letter code of the departure airport"
                    ),
                    "destination" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "3 letter code of the arrival airport"
                    ),
                    "user_id" to FunctionParameterProperty(
                        type = Type.INTEGER,
                        description = "Optional. ID of the user"
                    ),
                ),
                required = listOf("origin", "destination")
            )
        )
    }

    override fun use(args: Map<String, Any>): String {
        val origin = args["origin"]
        val destination = args["destination"]
        val dir = "/ai/agent/travel/data/packages"
        val filename = "$origin-$destination.json"
        val input = this::class.java.getResource("$dir/$filename")
        if (input != null) {
            val json = IOUtils.toString(input, "utf-8")
            return """
                Here is the packages found:
                ```json
                    $json
                ```
            """.trimIndent()
        } else {
            return """
                No package found for $args
            """.trimIndent()
        }
    }
}
