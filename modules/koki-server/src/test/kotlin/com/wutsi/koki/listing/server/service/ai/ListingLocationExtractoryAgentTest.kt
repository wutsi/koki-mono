package com.wutsi.koki.listing.server.service.ai

import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import org.junit.jupiter.api.Assertions.assertEquals
import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test

class ListingLocationExtractoryAgentTest {
    private val llm = Deepseek(
        apiKey = System.getenv("DEEPSEEK_API_KEY"),
        model = "deepseek-chat",
    )
    private val agent = ListingLocationExtractoryAgent("CM", llm)

    @Test
    fun run() {
        val text = """
            #Villa 3 Chambres à Louer | #Omnisports #Yaoundé #Cameroun
            📍 Quartier Omnisports – Yaoundé | villa rénovée | haut standing | mutation totale
            Caractéristiques :
            - 3 chambres autonomes
            - Cuisine américaine équipée
            - Toilettes visiteurs
            - Espace détente et barbecue 🍗
            - Parking pour 2 véhicules
            - Entièrement rénovée avec des matériaux soft et modernes
            💰 Loyer : 1.500.000 FCFA / mois
            📌 Commission : 5%
            📜 Transaction sécurisée devant notaire ou bailleur agréé
        """.trimIndent()
        val json = agent.run(text)
        val listing = JsonMapper().readValue(json, ListingLocationExtractoryResult::class.java)

        assertEquals("Yaoundé", listing.city)
        assertEquals("Omnisports", listing.neighbourhood)
        assertEquals("CM", listing.country)
    }
}
