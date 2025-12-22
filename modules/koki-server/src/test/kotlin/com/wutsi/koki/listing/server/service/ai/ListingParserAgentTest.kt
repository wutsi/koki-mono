package com.wutsi.koki.listing.server.service.ai

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.mockito.Mockito.mock
import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test

class ListingParserAgentTest {
    private val llm = Deepseek(
        apiKey = System.getenv("DEEPSEEK_API_KEY"),
        model = "deepseek-chat",
    )
    private val amenityService = mock<AmenityService>()
    private val city = LocationEntity(name = "Yaoundé", country = "CM")
    private val agent = ListingParserAgent(amenityService, city, llm)

    @BeforeEach
    fun setUp() {
        setupAmenities()
    }

    @Test
    fun tools() {
        assertEquals(0, agent.tools().size)
    }

    @Test
    fun systemInstructions() {
        assertEquals(null, agent.systemInstructions())
    }

    @Test
    fun apartment() {
        val text = """
            Ce magnifique appartement moderne au Stading !

            📍Situé à simbok ( Batibo)📍
            250 du rond point damas !
            3 chambres
            3 douches
            ✅ Parking
            ✅ forage 💦
            ✅ Lumière prépayé 💡
            ✅ Gardien
            ✅ Clim+ chauffe eau
            ✅ Balcon
            175.000fcfa. 658653143
        """.trimIndent()
        val json = agent.run(text)
        println("\n----\n" + json)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(true, listing["valid"])
        assertEquals(ListingType.RENTAL.name, listing["listingType"])
        assertEquals(PropertyType.APARTMENT.name, listing["propertyType"])
        assertEquals(ParkingType.PRIVATE.name, listing["parkingType"])
        assertEquals(3, listing["bedrooms"])
        assertEquals(3, listing["bathrooms"])
        assertEquals(175000, listing["price"])
        assertEquals("XAF", listing["currency"])
        assertEquals("+237658653143", listing["phone"])
        assertHasAmenityId(1001, listing)
        assertHasAmenityId(1003, listing)
        assertHasAmenityId(1004, listing)
        assertHasAmenityId(1006, listing)
        assertHasAmenityId(1052, listing)
        assertHasAmenityId(1059, listing)

        assertEquals(true, listing["street"]?.toString()?.contains("rond point damas"))
        assertEquals("simbok", listing["neighbourhood"])
        assertEquals("CM", listing["country"])
    }

    @Test
    fun `semi-furnished apartment width visit-fees`() {
        val text = """
            appartement  haut standing a louer a ahala barrière dans la barrière eau forages
            1 salon
            2 chambre
            douche
            cuisine aménagée avec cuisinière
            Prix 130000 f mois
            parking
            gardien
            Frais de visite 5000 f
            Commission 1 mois de loyer pour l'agent immobilier
            670660666
        """.trimIndent()
        val json = agent.run(text)
        println("\n----\n" + json)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(true, listing["valid"])
        assertEquals(ListingType.RENTAL.name, listing["listingType"])
        assertEquals(PropertyType.APARTMENT.name, listing["propertyType"])
        assertEquals(FurnitureType.SEMI_FURNISHED.name, listing["furnitureType"])
        assertEquals(2, listing["bedrooms"])
        assertEquals(1, listing["bathrooms"])
        assertEquals(130000, listing["price"])
        assertEquals(5000, listing["visitFees"])
        assertEquals("XAF", listing["currency"])
        assertEquals("+237670660666", listing["phone"])
        assertHasAmenityId(1004, listing)
        assertHasAmenityId(1011, listing)
        assertHasAmenityId(1059, listing)

//        assertEquals("Ahala Barrière", listing["city"])
//        assertEquals("Barrière Eau Forages", listing["neighbourhood"])
        assertEquals("Yaoundé", listing["city"])
        assertEquals("CM", listing["country"])
    }

    @Test
    fun land() {
        val text = """
            terrain en vente très bon prix
            idéal pour les stations services et autres investissements immobiliers
            -lieu de référence:montée collège mvogt
            -ville de Yaoundé
            -superficie 2000m²
            -TITRE FONCIER
            -prix: 120.000f/m²
            6 96 19 20 00 WHATSAPP POUR PLUS D'INFORMATIONS
        """.trimIndent()
        val json = agent.run(text)
        println("\n----\n" + json)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(true, listing["valid"])
        assertEquals(ListingType.SALE.name, listing["listingType"])
        assertEquals(PropertyType.LAND.name, listing["propertyType"])
        assertEquals(2000, listing["lotArea"])
        assertEquals(240000000, listing["price"])
        assertEquals(true, listing["phone"]?.toString()?.contains("696192000"))
        assertEquals(true, listing["hasLandTitle"])
        assertNotNull(listing["publicRemarks"])

        assertEquals("montée collège mvogt", listing["street"])
        assertEquals("Yaoundé", listing["city"])
        assertEquals("CM", listing["country"])
    }

    @Test
    fun commercial() {
        val text = """
            Fond de commerce d'un institut de beauté Mixte haut de gamme avec mezzanine en vente à jouvence en bordure de route. L'institut est entièrement équipé et prêt à usage
            - salle d'attente, douche
            - hammam
            - 03 salles de massage
            - cubitenaire 1000l avec surpresseur, caméra partout avec control à distance, climatisé de haut en bas
            - chaque équipement est haut de gamme et fonctionnel
            - plusieurs lits de massages
            - plusieurs tondeuses, casques
            - etc....
            🤖 PRIX : 9.500.000fr négociable
            ➡️ Loyer : 75.000fr
        """.trimIndent()
        val json = agent.run(text)
        println("\n----\n" + json)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(true, listing["valid"])
        assertEquals(ListingType.SALE.name, listing["listingType"])
        assertEquals(PropertyType.COMMERCIAL.name, listing["propertyType"])
        assertEquals(9500000, listing["price"])
        assertNotNull(listing["publicRemarks"])
        assertHasAmenityId(1000, listing)
        assertHasAmenityId(1006, listing)
        assertHasAmenityId(1005, listing)
        assertHasAmenityId(1058, listing)
    }

    @Test
    fun `invalid request`() {
        val text = """
            Hello Gentlemen! My name is Kara Winter!
            I'm a beautiful, submissive and well-educated 24 year old sweetheart.

            I provide safe and discreet incalls/outcalls to gentlemen within the Durham Region and surrounding areas.

            In my spare time I love to go horseback riding and compete in show jumping. I love all things equine, and hope to own a stable one day where I can train and sell my own horses.
        """.trimIndent()
        val json = agent.run(text)
        println("\n----\n" + json)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(false, listing["valid"])
        assertEquals(false, listing["reason"]?.toString()?.isEmpty())
    }

    private fun assertHasAmenityId(id: Int, listing: Map<*, *>) {
        val amenityIds = listing["amenityIds"] as List<Int>
        assertEquals(true, amenityIds.contains(id))
    }

    private fun setupAmenities() {
        val amenities = """
            id,amenity
            1000,Electricity
            1001,Prepaid electricity meter
            1002,Running Water
            1003,Hot Water
            1004,Water drilling
            1005,Cubitainer
            1006,Air Conditioning
            1007,Heating
            1008,Wi-Fi
            1009,Refrigerator
            1010,Microwave
            1011,Stove
            1012,Oven
            1013,Dishwasher
            1014,Coffee Maker
            1015,Cooking Basics
            1016,Dining Table
            1017,Toaster
            1018,Blender
            1019,Hair Dryer
            1020,Bath Towels
            1021,Bidet
            1022,Bathtub
            1023,Jacuzzi
            1024,Bed Linens
            1025,Pillows
            1026,Wardrobe
            1027,Closet
            1028,Dresser
            1029,Safe
            1030,Locker
            1031,TV
            1032,Cable/Satellite
            1033,Streaming Services
            1034,Sound System
            1035,Speakers
            1036,Board Games
            1037,Books
            1038,DVD Player
            1039,Video Game Console
            1040,Washing Machine
            1041,Dryer
            1042,Iron
            1043,Ironing Board
            1044,Vacuum Cleaner
            1045,Laundry Detergent
            1046,Swimming Pool
            1047,Garden
            1048,Yard
            1049,BBQ Grill
            1050,Patio
            1051,Rooftop
            1052,Balcony
            1053,Mountain view
            1054,Beach view
            1055,Beach Access
            1056,Ocean view
            1057,Hiking Trails
            1058,Security Cameras
            1059,Security guard
        """.trimIndent().split("\n")
            .drop(1)
            .map { line -> line.split(",") }
            .map { pair -> AmenityEntity(id = pair[0].toLong(), name = pair[1]) }

        doReturn(amenities).whenever(amenityService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }
}
