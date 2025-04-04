package com.wutsi.koki.platform.ai.agent.react.examples

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.ai.agent.DefaultAgent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.agent.react.ReactAgent
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.FunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.FunctionParameters
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.Type
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import org.apache.commons.io.IOUtils
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayOutputStream

fun main(args: Array<String>) {
    val agent = TravelAgent()

    // agent.poorGuy()

    // agent.anonymous(agent.gemini())
    // agent.anonymous(agent.deepseek())

    // agent.family(agent.gemini())
    // agent.family(agent.deepseek())

    // agent.romantic(agent.gemini())
    agent.romantic(agent.deepseek())
}

class TravelAgent {
    fun poorGuy() {
        react(
            """
                Hi!
                My name is John Smith.
                I want to goto Cancun in June and my budget is $1500
                Can you recommend me some offers?
            """.trimIndent()
        )
    }

    fun anonymous(llm: LLM) {
        run(
            query = """
                Hi!
                My name is Ray Sponsible.
                I want to goto Cancun in June with my wife for a max budget of $5000.
                Can you recommend me some offers?
            """.trimIndent(),
            llm = llm
        )
    }

    fun family(llm: LLM) {
        run(
            query = """
                Hi!
                My name is Herve Tchepannou, my email is herve.tchepannou@gmail.com.
                I want to goto Cancun in June with my wife and 3 kids who are 20, 15 and 6 years old, my budget is $10000.
                Can you recommend me some offers?
            """.trimIndent(),
            llm = llm
        )
    }

    fun romantic(llm: LLM) {
        run(
            query = """
                My email is herve.tchepannou@gmail.com, I'm looking for a place to rest and recharge with my girlfriend.
                Any suggestion?
            """.trimIndent(),
            llm = llm
        )
    }

    private fun react(query: String) {
        val agent = ReactAgent(
            llm = Gemini(
                apiKey = System.getenv("GEMINI_API_KEY"),
                model = "gemini-2.0-flash",
                rest = RestTemplate(),
            ),
            agentTools = listOf(
                BookingTool(),
                GeolocalizationTool(),
                PackagesTool(),
                ProfileTool(),
            ),
            query = """
                $query

                Aditional Instructions:
                  - If the traveller origin is not provided, resolve its origin using its email if provided
                  - When the email is provided, resolve the user's profile to find it's origin.
                  - When the email is not provided, use geo-localization service to resolve its origin.
                  - If you do not know the traveller's email, never try to assume it for resolving its profile!

                When you identify multiple destinations, search packages for each destination before making your final recommendation.

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

    fun deepseek(): LLM {
        return Deepseek(
            apiKey = System.getenv("DEEPSEEK_API_KEY"),
            model = "deepseek-chat",
            rest = RestTemplate(),
            objectMapper = ObjectMapper(),
        )
    }

    fun gemini(): LLM {
        return Gemini(
            apiKey = System.getenv("GEMINI_API_KEY"),
            model = "gemini-2.0-flash",
            rest = RestTemplate(),
        )
    }

    private fun run(query: String, llm: LLM) {
        val agent = DefaultAgent(
            llm = llm,
            tools = listOf(
                BookingTool(),
                GeolocalizationTool(),
                PackagesTool(),
                ProfileTool(),
                ThemeDestinationTool(),
            ),
            maxIterations = 10,
            systemInstructions = """
                You are a Travel Agent assistant helping traveller to find the best trip so that they can have memorable experiences.

                Instructions:
                  - If the traveller origin is not provided, resolve its origin using its email if provided, otherwise use geo-localization.
                  - When possible, fetch the traveller past bookings to understand his preferences (air carrier, hotel, hotel ratings etc.).
                  - If you find multiple potential destinations for the trip, search packages to each of the destination then combine all offers before making your recommendation
                  - Sort the offers based on
                    1. traveller preferences (from past bookings) when ever possible
                    2. hotel rating
                    3. number of ratings
                    4. budget
                  - Recommend a maximum of 3 offers.
                  - Return the result in JSON format that looks like this:
                  ```json
                    {
                      "offers":[
                        {
                          "offer": {
                            "start_date": "2025-06-05",
                            "end_date": "2025-06-12",
                            "total_price": 5400,
                            "flight":{
                              "air_carrier_code": "AC",
                              "air_carrier": "Air Canada",
                              "cabin_code": "Business",
                              "origin": "YUL",
                              "destination":  "CUN"
                            },
                            "hotel":{
                              "id": "32039209302",
                              "name": "Hyatt Ziva Cancun",
                              "rating": 4.7,
                              "number_of_ratings": 4125,
                              "image_url": "https://example.com/hotels/hyatt_ziva.jpg",
                              "short_description": "Luxury all-inclusive with stunning ocean views",
                              "amenities": [
                                "Private beach area",
                                "Gourmet restaurants",
                                "24-hour room service",
                                "Swim-up suites",
                                "Dolphin experience"
                              ]
                            }
                          },
                          "reason": "Explanation of why you chose this tool"
                        }
                      ]
                    }
                  ```
            """.trimIndent(),
        )

        val output = ByteArrayOutputStream()
        agent.run(query, output)
        println(String(output.toByteArray()))
    }
}

class GeolocalizationTool : Tool {
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

class ProfileTool : Tool {
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
            return "Here is the profile data of $email:\n```json\n$json\n```"
        }
    }
}

class BookingTool : Tool {
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
            return "Here are the bookings of the traveller:\n```json\n$json```"
        } else {
            return "No past booking found for $input"
        }
    }
}

class PackagesTool : Tool {
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
                    "number_of_adults" to FunctionParameterProperty(
                        type = Type.INTEGER,
                        description = "Optional. Number of adults"
                    ),
                    "number_of_children" to FunctionParameterProperty(
                        type = Type.INTEGER,
                        description = "Optional. Number of children"
                    ),
                ),
                required = listOf("origin", "destination")
            )
        )
    }

    override fun use(args: Map<String, Any>): String {
        var origin = args["origin"].toString()
        if (origin == "YMQ") {
            origin = "YUL"
        }
        var destination = args["destination"].toString()

        val dir = "/ai/agent/travel/data/packages"
        val filename = "$origin-$destination.json"
        val input = this::class.java.getResource("$dir/$filename")
        if (input != null) {
            val json = IOUtils.toString(input, "utf-8")
            return "Here is the packages found from $origin to $destination:\n```json\n$json```"
        } else {
            return """
                No package found from $origin to $destination
            """.trimIndent()
        }
    }
}

class ThemeDestinationTool : Tool {
    override fun function(): FunctionDeclaration {
        return FunctionDeclaration(
            name = "search_destination_per_theme",
            description = "Return a list of destination for a given theme",
            parameters = FunctionParameters(
                properties = mapOf(
                    "theme" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "Theme of the destination",
                        enum = listOf(
                            "BEACHES",
                            "ADVENTURE_OUTDOORS",
                            "HERITAGE_CULTURE",
                            "NATURE_LANDSCAPE",
                            "WILDLIFE_SAFARI",
                            "WINE_FOOD",
                        )
                    ),
                ),
                required = listOf("theme")
            )
        )
    }

    override fun use(args: Map<String, Any>): String {
        val theme = args["theme"]
        val destinations = when (theme) {
            "BEACHES" -> "Punta Cana, Cancun"
            "WINE_FOOD" -> "Paris, Cape Town"
            "WILDLIFE_SAFARI" -> "Windhoek, Mombassa"
            else -> "Cancun"
        }
        return "The destination(s) for the theme $theme are: $destinations"
    }
}
