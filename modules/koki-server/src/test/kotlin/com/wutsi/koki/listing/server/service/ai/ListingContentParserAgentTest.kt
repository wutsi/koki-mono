package com.wutsi.koki.listing.server.service.ai

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.mockito.Mockito.mock
import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test

class ListingContentParserAgentTest {
    private val llm = Deepseek(
        apiKey = System.getenv("DEEPSEEK_API_KEY"),
        model = "deepseek-chat",
    )
    private val amenityService = mock<AmenityService>()
    private val locationService = mock<LocationService>()
    private val city = LocationEntity(id = 111, name = "Yaoundé", country = "CM")
    private val agentUser = UserEntity(
        id = 333,
        displayName = "Ray Sponsible",
        employer = "REIMAX",
        street = "3030 Linton",
        cityId = city.id,
        mobile = "+237673485325"
    )
    private val agent = ListingContentParserAgent(amenityService, locationService, city, agentUser, llm)

    @BeforeEach
    fun setUp() {
        setupAmenityService()
        setupLocationService()
    }

    @Test
    fun apartment() {
        val text = """
            Ce magnifique appartement moderne haut Standing !

            📍Situé à simbok (Batibo)📍
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
//        assertEquals("+237658653143", listing["phone"])
        assertHasAmenityId(1001, listing)
//        assertHasAmenityId(1004, listing)
//        assertHasAmenityId(1006, listing)
//        assertHasAmenityId(1052, listing)
//        assertHasAmenityId(1059, listing)

//        assertEquals(true, listing["street"]?.toString()?.contains("rond point damas"))
        assertEquals("Simbock", listing["neighbourhood"])
        assertEquals(237049, listing["neighbourhoodId"])
        assertEquals("CM", listing["country"])
    }

    @Test
    fun `prompt with address of agent and property`() {
        val text = """
            Studio à louer : Odza (80 000 Fcfa/mois)

            Ville : Yaoundé
            Quartier : Odza
            Adresse: 333 Avenue de Gaule
            Style de maison : Immeuble

            -1 Salon
            -1 Chambre
            -1 Cuisine
            -1 Douche

            Loyer : 80 000 Fcfa
            Avance  : 12 Mois


            ${agentUser.displayName?.uppercase()}
            Jours : Lundi - Samedi
            Heures : 08h00 - 18h00
            Bureau : ${city.name}, ${agentUser.street}. Immeuble carrelé noir et blanc en face UBA, "Centre Commercial EKOTTO". Porte N° 302
            ${agentUser.mobile}
        """.trimIndent()
        val json = agent.run(text)
        println("\n----\n" + json)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals("333 Avenue de Gaule", listing["street"])
        assertEquals("Yaoundé", listing["city"])
        assertEquals("Odza", listing["neighbourhood"])
    }

    @Test
    fun `prompt with address of agent only`() {
        val text = """
            Studio à louer (80 000 Fcfa/mois)

            Ville : Yaoundé
            Style de maison : Immeuble

            -1 Salon
            -1 Chambre
            -1 Cuisine
            -1 Douche

            Loyer : 80 000 Fcfa
            Avance  : 12 Mois


            ${agentUser.displayName?.uppercase()}
            Jours : Lundi - Samedi
            Heures : 08h00 - 18h00
            Bureau : ${city.name}, ${agentUser.street}. Immeuble carrelé noir et blanc en face UBA, "Centre Commercial EKOTTO". Porte N° 302
            ${agentUser.mobile}
        """.trimIndent()
        val json = agent.run(text)
        println("\n----\n" + json)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(null, listing["street"])
        assertEquals("Yaoundé", listing["city"])
        assertEquals(null, listing["neighbourhood"])
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
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(true, listing["valid"])
        assertEquals(ListingType.RENTAL.name, listing["listingType"])
        assertEquals(PropertyType.APARTMENT.name, listing["propertyType"])
        // assertEquals(FurnitureType.SEMI_FURNISHED.name, listing["furnitureType"])
        assertEquals(2, listing["bedrooms"])
        assertEquals(1, listing["bathrooms"])
        assertEquals(130000, listing["price"])
        assertEquals(5000, listing["visitFees"])
        assertEquals("XAF", listing["currency"])
//        assertEquals("+237670660666", listing["phone"])
//        assertHasAmenityId(1004, listing)
//        assertHasAmenityId(1011, listing)
//        assertHasAmenityId(1059, listing)

        assertEquals("Yaoundé", listing["city"])
        assertEquals("Ahala", listing["neighbourhood"])
        assertEquals(237094, listing["neighbourhoodId"])
        assertEquals("Yaoundé", listing["city"])
        assertEquals("CM", listing["country"])
        assertNull(listing["landTitle"])
        assertNull(listing["technicalFile"])
        assertNull(listing["transactionWithNotary"])
        assertNull(listing["mutationType"])
        assertNull(listing["numberOfSigners"])
    }

    @Test
    fun land() {
        val text = """
            terrain titré en vente très bon prix
            idéal pour les stations services et autres investissements immobiliers
            -lieu de référence:montée collège mvogt
            -ville de Yaoundé
            -superficie 2000m²
            -TITRE FONCIER
            -Borné
            -prix: 120.000f/m²
            6 96 19 20 00 WHATSAPP POUR PLUS D'INFORMATIONS
            Transaction sécurisée devant notaire ou bailleur agréé
            1 signataire uniquement
            mutation totale
            Dossier technique disponible
        """.trimIndent()
        val json = agent.run(text)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(true, listing["valid"])
        assertEquals(ListingType.SALE.name, listing["listingType"])
        assertEquals(PropertyType.LAND.name, listing["propertyType"])
        assertEquals(2000, listing["lotArea"])
        assertEquals(240000000, listing["price"])
        assertNotNull(listing["publicRemarks"])

        // assertEquals("montée collège mvogt", listing["street"])
        assertEquals("Yaoundé", listing["city"])
//        assertEquals(null, listing["neighbourhood"])
//        assertEquals(null, listing["neighbourhoodId"])
        assertEquals("CM", listing["country"])
        assertEquals(true, listing["landTitle"])
        assertEquals(true, listing["technicalFile"])
        assertEquals(true, listing["transactionWithNotary"])
        assertEquals("TOTAL", listing["mutationType"])
        assertEquals("DEMARCATED", listing["fenceType"])
        assertEquals(1, listing["numberOfSigners"])
//        assertEquals(null, listing["subdivided"])
        assertEquals(null, listing["morcelable"])
    }

    @Test
    fun `land subdivided`() {
        val text = """
            terrain de 2 hectares titré, loti et clôturé en vente a 1500f/m², situé a nkolbisson.
            Possibilite de morceler le terrain selon les besoins de l'acheteur (minimum 500m2 par lot).
        """.trimIndent()
        val json = agent.run(text)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(true, listing["valid"])
        assertEquals(ListingType.SALE.name, listing["listingType"])
        assertEquals(PropertyType.LAND.name, listing["propertyType"])
        assertEquals(20000, listing["lotArea"])
        assertEquals(30000000, listing["price"])

        assertEquals(true, listing["landTitle"])
        assertEquals(true, listing["subdivided"])
        assertEquals(true, listing["morcelable"])
        assertEquals("FENCED", listing["fenceType"])
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
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(true, listing["valid"])
        assertEquals(ListingType.SALE.name, listing["listingType"])
        assertEquals(PropertyType.COMMERCIAL.name, listing["propertyType"])
        assertEquals(9500000, listing["price"])
        assertNotNull(listing["publicRemarks"])
        assertEquals("Yaoundé", listing["city"])
        assertEquals("Jouvence", listing["neighbourhood"])
        assertEquals(237096, listing["neighbourhoodId"])
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

    @Test
    fun `modern villa`() {
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
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(true, listing["valid"])
        assertEquals("RENTAL", listing["listingType"])
        assertEquals("VILLA", listing["propertyType"])
        assertEquals(3, listing["bedrooms"])
//        assertEquals(3, listing["bathrooms"])
//        assertEquals(1, listing["halfBathrooms"])
        assertEquals(3, listing["bedrooms"])
        assertEquals(2, listing["parkings"])
        assertEquals("PRIVATE", listing["parkingType"])
        assertEquals(5.0, listing["commission"])
        assertEquals("Omnisports", listing["neighbourhood"])
        assertEquals(237097, listing["neighbourhoodId"])
        assertEquals("Yaoundé", listing["city"])
        assertEquals("CM", listing["country"])

        assertHasAmenityId(1011, listing)
//        assertHasAmenityId(1012, listing)
//        assertHasAmenityId(1049, listing)
    }

    @Test
    fun `daily rental`() {
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
            💰 Loyer : 50.000 FCFA / jour
            📌 Commission : 5%
            📜 Transaction sécurisée devant notaire ou bailleur agréé
        """.trimIndent()
        val json = agent.run(text)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(false, listing["valid"])
    }

    @Test
    fun `weekly rental`() {
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
            💰 Loyer : 300.000 FCFA / semaine
            📌 Commission : 5%
            📜 Transaction sécurisée devant notaire ou bailleur agréé
        """.trimIndent()
        val json = agent.run(text)
        val listing = JsonMapper().readValue(json, Map::class.java)

        assertEquals(false, listing["valid"])
    }

    @Test
    fun test() {
        val text = """
            Somptueux #Duplex À VENDRE #400Millions Fcfa
            📍Quartier #Odza Yaoundé
            🔺Superficie : 1000 m² (Mutation Totale)
            Il s’agit d’Une grande Villa-Duplex (R+1) de 5 chambres autonomes avec 3 salons au quartier Odza à Yaoundé
            5 Chambres autonomes
            5 Salles de bain et un 🚾 Visiteur
            Grande Cuisine avec Buanderie
            1 grand espace vert avec possibilité de Faire une Piscine
            1 guérite pour agent de sécurité
            Dépendance avec 2 Chambres autonomes supplémentaires
        """.trimIndent()
        agent.run(text)
    }

    private fun assertHasAmenityId(id: Int, listing: Map<*, *>) {
        val amenities = listing["amenities"] as List<Map<String, Any>>
        val amenityIds = amenities.mapNotNull { amenity -> amenity["id"] }
        assertEquals(true, amenityIds.contains(id))
    }

    private fun setupAmenityService() {
        val amenities = """
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

    private fun setupLocationService() {
        configureCity()
        configureNeighbourhoods()
    }

    private fun configureCity() {
        val city = LocationEntity(id = 3333, name = "Yaoundé", type = LocationType.CITY, country = "CM")
        doReturn(listOf(city)).whenever(locationService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            eq(listOf(LocationType.CITY)),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }

    private fun configureNeighbourhoods() {
        val neighbourhoods = """
            237000,Centre Ville,Yaoundé,3.8692921999551375, 11.518550404521816
            237001,Nlongkak,Yaoundé,3.879225806692496, 11.5203528488888
            237002,Bastos,Yaoundé,3.8925339696406587, 11.511400038328873
            237003,Mvog-Mbi,Yaoundé,3.8505983099639973, 11.520835565315775
            237004,Messa,Yaoundé,3.8889192886555275, 11.490254143404025
            237005,Nkol Bikok,Yaoundé,3.867194138795373, 11.48353148539188
            237006,Obili,Yaoundé,3.8610223711151064, 11.490102484631674
            237007,Mvog-Ada,Yaoundé,3.8646694021601893, 11.528544711796718
            237008,Mokolo,Yaoundé,3.876712498953334, 11.498279702722225
            237009,Cité Verte,Yaoundé,3.8745694436432334, 11.48908925925084
            237010,Etoa-Meki,Yaoundé,3.8814522887439127, 11.529107578671285
            237011,Nsimeyong,Yaoundé,3.8354965823797196, 11.49478741806351
            237012,Mimboman,Yaoundé,3.8695939924143374, 11.554617894151319
            237013,Melen,Yaoundé,3.8668944153485434, 11.500740537562356
            237014,Odza,Yaoundé,3.8061519579544085, 11.530026769573444
            237015,Tsinga,Yaoundé,3.884192566286806, 11.506577024084004
            237016,Kondengui,Yaoundé,3.852664327526627, 11.526702987404587
            237017,Essos,Yaoundé,3.8706011576190646, 11.542179106021013
            237018,Ekounou,Yaoundé,3.844070467036341, 11.534578444489384
            237019,Elig-Essono,Yaoundé,3.8741682967954127, 11.525634137862982
            237020,Nkolndongo,Yaoundé,3.85841452321584, 11.527079109493366
            237021,Biyem-Assi,Yaoundé,3.8409252554355082, 11.48740552328444
            237022,Etoudi,Yaoundé,3.850382870033124, 11.524779567164344
            237023,Olezoa,Yaoundé,3.8684843663646578, 11.479348238328864
            237024,Ngousso,Yaoundé,3.8343850925488248, 11.574487755519542
            237025,Efoulan,Yaoundé,3.8357093517749785, 11.505732516047082
            237026,Nkolmesseng,Yaoundé,3.8806045485951848, 11.553448378442988
            237027,Anguissa,Yaoundé,3.86177548167101, 11.53496387813675
            237029,Nkoabang,Yaoundé,3.860173090541269, 11.591600380657903
            237030,Titi-Gare,Yaoundé,3.883709688468779, 11.55293241134199
            237033,Nkoléwé,Yaoundé,3.861441943259163, 11.534146515100572
            237034,Ekoudou,Yaoundé,3.8789970169290022, 11.512152516211234
            237035,Briquetterie,Yaoundé,3.8742162006313277, 11.509109029266194
            237036,Carrière,Yaoundé,3.8749663736688214, 11.480777609493382
            237037,Fouda,Yaoundé,3.880109042772849, 11.48114291134201
            237039,Mbankolo,Yaoundé,3.89833153015749, 11.494446916216232
            237040,Nkol-Eton,Yaoundé,3.8964619733881256, 11.510616175497084
            237041,Nkolbisson,Yaoundé,3.8715978026955717, 11.454169718757758
            237043,Nkozoa,Yaoundé,3.86193685126322, 11.520191645075593
            237049,Simbock,Yaoundé,3.817442071855248, 11.466151818062976
            237050,Mont-Fébé,Yaoundé,3.9128975572913878, 11.491551711341954
            237053,Ekoumdoum,Yaoundé,3.822049270861789, 11.537224002721045
            237054,Mbankomo,Yaoundé,3.79022739543727, 11.39985751754894
            237057,Nsimalen,Yaoundé,3.714851690661909, 11.547785538328853
            237061,Nkom Kana,Yaoundé,3.8893496852212657, 11.495014951356179
            237062,Ngoa-Ekélé,Yaoundé,3.8558127937728455, 11.499003929918695
            237063,Elig-Edzoa,Yaoundé,3.8487398324650655, 11.501658549239048
            237064,Olembé,Yaoundé,3.949564163592176, 11.537187383239027
            237065,Mvolyé,Yaoundé,3.83494509428093, 11.500688568651837
            237066,Mendong,Yaoundé,3.8453855704955826, 11.519096945050551
            237067,Mballa II,Yaoundé,3.8979032623760954, 11.523958960393882
            237069,Etam-Bafia,Yaoundé,3.851104056052318, 11.525681151822313
            237071,Tongolo,Yaoundé,3.908969006138005, 11.526837762576633
            237072,Hippodrome,Yaoundé,3.8720832599970314, 11.51542349163051
            237074,Nkomo,Yaoundé,3.8439009619381577, 11.534775497848502
            237078,Biteng,Yaoundé,3.8511143559696825, 11.557325892782528
            237079,Quartier du Lac,Yaoundé,3.8605105869338567, 11.51170337371814
            237080,Madagascar,Yaoundé,3.88213735896351, 11.487350950836198
            237081,Oyom Abang,Yaoundé,3.8797590550308114, 11.469105903464179
            237083,Etoug-Ebe,Yaoundé,3.8621611703010164, 11.520257083243541
            237084,Soa,Yaoundé,3.975649238605864, 11.593469560423955
            237087,Obobogo,Yaoundé,3.824980036487706, 11.502124108819377
            237088,Mefou,Yaoundé,3.8507337514971725, 11.521817682506454
            237089,Mvan,Yaoundé,3.8121649503152484, 11.522317901055626
            237090,Damas,Yaoundé,3.825833760195713, 11.505503282506421
            237091,Messassi,Yaoundé,3.9033646491835947, 11.546101818064955
            237092,Santa Barbara,Yaoundé,3.9155418074281303, 11.530948253671024
            237093,Emana,Yaoundé,3.9216, 11.5375
            237094,Ahala,Yaoundé,3.794539959971708, 11.495552753670934
            237095,Nkolfoulou,Yaoundé,3.916956241351669, 11.576112211342009
            237096,Jouvence,Yaoundé,3.8241033731300993, 11.480716891881322
            237097,Omnisports,Yaoundé,3.8823422424592544, 11.540692411341988
        """.trimIndent().split("\n")
            .drop(1)
            .map { line -> line.split(",") }
            .map { pair -> LocationEntity(id = pair[0].toLong(), name = pair[1]) }

        doReturn(neighbourhoods).whenever(locationService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            eq(listOf(LocationType.NEIGHBORHOOD)),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }
}
