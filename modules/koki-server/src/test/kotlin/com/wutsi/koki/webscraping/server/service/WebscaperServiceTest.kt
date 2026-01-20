package com.wutsi.koki.webscraping.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteRequest
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.domain.WebsiteEntity
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class WebscaperServiceTest {
    private val webpageService = mock<WebpageService>()
    private val http = mock<Http>()
    private val service: WebscaperService = WebscaperService(webpageService, http)

    private var id = System.currentTimeMillis()
    private val hash = "b32e5c02a626c9505a0f6ad797b92a3f"
    private val request = ScrapeWebsiteRequest(testMode = false)

    @BeforeEach
    fun setUp() {
        doReturn(hash).whenever(http).hash(any())

        doReturn(WebpageEntity(id = ++id))
            .whenever(webpageService)
            .new(any(), any(), any(), any())

        doAnswer { invocation -> invocation.arguments[0] }
            .whenever(webpageService)
            .save(any())
    }

    @Test
    fun `test mode`() {
        // GIVEN
        val website = setupWebsite(
            name = "codecis",
            baseUrl = "https://codecis.com",
            homeUrl = "https://codecis.com/status/a-louer/",
            listingUrlPrefix = "/property",
            contentSelector = ".property-description-wrap, .property-features-wrap, property-address-wrap",
            imageSelector = "#property-gallery-js img",
        )

        // WHEN
        service.scrape(website, ScrapeWebsiteRequest(testMode = true))

        // THEN
        verify(webpageService, atLeast(1))
            .new(any(), any(), any(), anyOrNull())

        verify(webpageService, never()).save(any())
    }

    @Test
    fun codecis() {
        // GIVEN
        val website = setupWebsite(
            name = "codecis",
            baseUrl = "https://codecis.com",
            homeUrl = "https://codecis.com/status/a-louer/",
            listingUrlPrefix = "/property",
            contentSelector = ".property-title-price-wrap, .property-description-wrap, .property-features-wrap, property-address-wrap",
            imageSelector = "#property-gallery-js img",
        )

        // WHEN
        val webpages = service.scrape(website, request)
        assertEquals(12, webpages.size)

        // THEN
        val url = argumentCaptor<String>()
        val images = argumentCaptor<List<String>>()
        val content = argumentCaptor<String>()
        verify(webpageService, times(webpages.size))
            .new(
                eq(website),
                url.capture(),
                images.capture(),
                content.capture(),
            )

        assertEquals(
            "https://codecis.com/property/residence-de-type-ambassadeur-r2-de-800-m2-a-louer-sise-a-bastos-derriere-la-residence-du-nigeria-acces-bitume/",
            url.firstValue
        )
        assertEquals(18, images.firstValue.size)
        assertEquals(
            """
Résidence de type Ambassadeur ,Bastos
=====================================

* 7 000 000FCFA

Description
-----------

R+2 de 800 m2 à louer, sis à Bastos derriere la Résidence du Nigéria, accès bitumé, ayant la configuration suivante :

* Au rez-de-chaussée 01 hall d'entrée de 05 m2; 01 bar de 05 m2; 01 cuisine de 41 m2; 01 salle à manger de 24 m2; 02 séjours de 30 m2 chacun; 01 toilette visiteur de 05 m2;
* Au 1er étage : 01 bibliothèque de 17 m2; 01 chambre principale de 25 m2 avec dressing de 11 m2; 01 salle de bain complète de 11 m2; 02 chambres avec 02 salles d'eau incluses de 28 m2; 01 séjour lumineux et climatisé de 20 m2; 01 petite cuisine de 08 m2;
* Au 2e étage 02 vastes chambres avec salles d'eau incluses de 31 et 26 m2 01 palier de 12 m2

-Sous-sol: 02 chambres respectivement de 12 m2 et 09 m2 , 01 salle d'eau, 03 magasins, 01 refuge, 01 sas ,01 salle de sport et ses vestiaires de 60 m2

️01 Buanderie, local GE, 01 sas , 01 piscine ( en construction) , eau courante et forage

**Loyer: 07 millions le mois Conditions d'entrée : 02 ans d'avance de loyer, 02 mois de caution.**

Extras: 01 Buanderie ; 01 cave à vins, local GE, Salle de sport et ses vestiaires de 57 m2 ; 02 magasins , 02 parkings couvert et ouvert; 01 piscine en (construction)

Features
--------

* [Climatisation](https://codecis.com/feature/climatisation/)
* [Douche extérieure](https://codecis.com/feature/douche-exterieure/)
* [Eau chaude](https://codecis.com/feature/eau-chaude/)
* [Jardin](https://codecis.com/feature/jardin/)
* [Machine à laver](https://codecis.com/feature/machine-a-laver/)
* [Parking](https://codecis.com/feature/parking/)
* [Piscine](https://codecis.com/feature/piscine/)
* [Salle de sport](https://codecis.com/feature/salle-de-sport/)
* [Séchoir](https://codecis.com/feature/sechoir/)
* [TV Cable](https://codecis.com/feature/tv-cable/)

            """.trimIndent(), content.firstValue
        )

        verify(webpageService, times(webpages.size)).save(any())
    }

    @Test
    fun mav_sarl() {
        // GIVEN
        val website = setupWebsite(
            name = "mav-sarl",
            baseUrl = "https://mav-sarl.com/",
            homeUrl = "https://mav-sarl.com/featured/",
            listingUrlPrefix = "https://mav-sarl.com/properties/",
            contentSelector = "h1, .mh-estate__section h3, .mh-estate__section p, .mh-estate__section .mh-estate__list",
            imageSelector = ".swiper-wrapper img",
        )

        // WHEN
        val webpages = service.scrape(website, request)
        assertEquals(13, webpages.size)

        // THEN
        val url = argumentCaptor<String>()
        val images = argumentCaptor<List<String>>()
        val content = argumentCaptor<String>()
        verify(webpageService, times(webpages.size))
            .new(
                eq(website),
                url.capture(),
                images.capture(),
                content.capture(),
            )

        assertEquals(
            "https://mav-sarl.com/properties/duplex/sangmelima/duplex-de-haut-standing-a-vendre-ou-a-louer-a-sangmelima-quartier-monavobe-%f0%9f%87%a8%f0%9f%87%b2/",
            url.firstValue
        )
        assertEquals(9, images.firstValue.size)
        assertEquals(
            """
Luxury duplex for sale or rent in Sangmélima -- Monavobé district

* **Property type:** [Duplex](https://mav-sarl.com/property-type/duplex/ "Duplex")
* **Offer type:** [For rent](https://mav-sarl.com/offer-type/a-louer/ "For rent") For [sale](https://mav-sarl.com/offer-type/a-vendre/ "For sale") , [On hold](https://mav-sarl.com/offer-type/en-attente/ "On hold")
* **City:** [Sangmélima](https://mav-sarl.com/ville/sangmelima/ "Sangmelima")
* **Neighborhood:** [Monavobé](https://mav-sarl.com/quartier/monavobe/ "Monavobé")

Features

* 2 spacious dining rooms
* 3 balconies with unobstructed views
* 3 large bedrooms in an outbuilding
* 4 modern showers
* 4 large, bright living rooms (upstairs \& downstairs)
* Boucarreau for your receptions and moments of relaxation
* Solar electricity + large generator
* Large outdoor kitchen
* Solar streetlights throughout the camp
* Olympic swimming pool
* Safety and peace of mind guaranteed
* A fully equipped gym

Details

Luxury duplex for sale or rent in Sangmélima -- Monavobé district, located behind the former MINDEF residence

Treat yourself to an exceptional setting in a secure 8,000 m² estate, perfect for a private residence or your special events. This spacious and elegant duplex guarantees comfort, privacy, and luxury.

Main features: 4 large, bright living rooms (upstairs \& downstairs), 2 spacious dining rooms, 3 balconies with unobstructed views , 4 modern showers, a fully equipped gym, a large outdoor kitchen, 3 large bedrooms in separate buildings, and the Boucarreau Olympic-size swimming pool for your receptions and relaxation.

Amenities \& comfort:

Solar electricity + large generator

Solar streetlights throughout the camp

Safety and peace of mind guaranteed

Rental options: For parties, weddings, birthdays, and ceremonies ; for short stays with family or friends. Friendly atmosphere, spacious and well-maintained space.

Ideal for personal residence or prestigious rental investment.

Visits by appointment only. Contact us now to book or for more information!

+237 694633002 Call or WhatsApp

            """.trimIndent(), content.firstValue
        )

        verify(webpageService, times(webpages.size)).save(any())
    }

    @Test
    fun mapiole() {
        // GIVEN
        val website = setupWebsite(
            name = "mapiole",
            baseUrl = "https://mapiole.com",
            homeUrl = "https://mapiole.com/product-listing?Sort=id+DESC&page=1",
            listingUrlPrefix = "/yaoundé",
            contentSelector = "h1, h2, .lh-lg",
            imageSelector = "#lightboxExampleCarousel .carousel-inner img",
        )

        // WHEN
        val webpages = service.scrape(website, request)
        assertEquals(11, webpages.size)

        // THEN
        val url = argumentCaptor<String>()
        val images = argumentCaptor<List<String>>()
        val content = argumentCaptor<String>()
        verify(webpageService, times(webpages.size))
            .new(
                eq(website),
                url.capture(),
                images.capture(),
                content.capture(),
            )

        assertEquals(
            "https://mapiole.com/Yaoundé/Apartment/APPARTEMENT-2-CHAMBRES-À-LOUER-À-BASTOS-1363",
            url.firstValue
        )
        assertEquals(8, images.firstValue.size)
        assertEquals(
            """
**APPARTEMENT 2 CHAMBRES À LOUER À BASTOS**

550.000 FCFA

Appartement haut standing à louer au quartier Bastos dans une zone résidentielle, calme et sécurisée a proximité des ambassades . Caractéristiques techniques du bien : -01 Salon spacieux , climatisé, lumineux avec balcon . -01 Cuisine avec plan de travail, placard de rangement, balcon et buanderie -02 chambres climatisées avec placard et balcons. -02 Douches avec eau chaude et baignoire dans la chambres principale -01 WC visiteurs. Gardien H24 , caméra de surveillance Groupe électrogène Réserve d'eau Parking disponibles. Accès facile et à proximité de l'axe principale

4.5

            """.trimIndent(), content.firstValue
        )

        verify(webpageService, times(webpages.size)).save(any())
    }

    @Test
    fun ereshome() {
        // GIVEN
        val website = setupWebsite(
            name = "ereshome",
            baseUrl = "https://www.ereshomes.com/",
            homeUrl = "https://www.ereshomes.com/property-type/apartments",
            listingUrlPrefix = "/property-details",
            contentSelector = ".sp-lg-title, .price, .ps-widget h4, .ps-widget p.text, .ps-widget p.justify",
            imageSelector = ".sp-img-content img",
        )

        // WHEN
        val webpages = service.scrape(website, request)
        assertEquals(12, webpages.size)

        // THEN
        val url = argumentCaptor<String>()
        val images = argumentCaptor<List<String>>()
        val content = argumentCaptor<String>()
        verify(webpageService, times(webpages.size))
            .new(
                eq(website),
                url.capture(),
                images.capture(),
                content.capture(),
            )

        assertEquals(
            "https://www.ereshomes.com/property-details/luxury-pente-house-for-rent",
            url.firstValue
        )
        assertEquals(5, images.firstValue.size)
        assertEquals(
            """
luxury pente house for rent

5 000 000 XAF /month

Overview

Apartments

For Rent

Residential

3

3

sqft

Property Description

-01 fully equipped living and dining room -01 fully equipped kitchen -03 fully equipped bedrooms -03 bathrooms -half bathroom... -01 fully equipped living and dining room -01 fully equipped kitchen -03 fully equipped bedrooms -03 bathrooms -half bathroom

Property details

PO-6L9OIWN2

5 000 000 XAF /month

3

3

Apartments

For Rent

Residential

Location

SHILOH SUITES

Yaoundé

Cameroon

Elig Essono

Centre

Features \& Amenities

Swimming pool

Air conditioning

Terrace/Balcony

Wifi

TV cable

Laundry

Fence

Refrigerator

Oven

Washmachine

Dryer

Microwave

Water heater

Audio video system

Security system

            """.trimIndent(), content.firstValue
        )

        verify(webpageService, times(webpages.size)).save(any())
    }

    private fun setupWebsite(
        name: String,
        baseUrl: String,
        homeUrl: String,
        listingUrlPrefix: String,
        contentSelector: String?,
        imageSelector: String,
    ): WebsiteEntity {
        doAnswer { invocation ->
            if (invocation.arguments[0] == homeUrl) {
                getHtml("/webscraping/sites/$name/home.html")
            } else {
                getHtml("/webscraping/sites/$name/listing.html")
            }
        }
            .whenever(http)
            .get(any())

        return WebsiteEntity(
            id = 111L,
            tenantId = 1L,
            baseUrl = baseUrl,
            homeUrls = listOf(homeUrl),
            listingUrlPrefix = listingUrlPrefix,
            contentSelector = contentSelector,
            imageSelector = imageSelector
        )
    }

    private fun getHtml(path: String): String {
        return WebscaperServiceTest::class.java
            .getResourceAsStream(path)!!
            .bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}
