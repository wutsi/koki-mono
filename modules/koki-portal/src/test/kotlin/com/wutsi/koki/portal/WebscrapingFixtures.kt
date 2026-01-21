package com.wutsi.koki.portal

import com.wutsi.koki.ListingFixtures
import com.wutsi.koki.webscraping.dto.Webpage
import com.wutsi.koki.webscraping.dto.WebpageSummary

object WebscrapingFixtures {
    val webpage = Webpage(
        id = 111,
        listingId = ListingFixtures.listings[0].id,
        websiteId = 1L,
        url = "https://www.realtor.com/property/dflk032/appartment-for-rent",
        active = true,
        imageUrls = listOf("https://picsum.photos/800/600", "https://picsum.photos/600", "https://picsum.photos/300"),
        content = """
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
        """.trimIndent()
    )

    val webpages = listOf(
        WebpageSummary(
            id = 111,
            listingId = ListingFixtures.listings[0].id,
            websiteId = 1L,
            url = "https://www.realtor.com/property/dflk032/appartment-for-rent",
            active = true,
            imageUrl = "https://picsum.photos/800/600",
        ),
        WebpageSummary(
            id = 112,
            listingId = ListingFixtures.listings[0].id,
            websiteId = 1L,
            url = "https://www.realtor.com/property/5409f/appartment-for-rent-bastos",
            active = true,
            imageUrl = "https://picsum.photos/800/600",
        ),
        WebpageSummary(
            id = 113,
            listingId = ListingFixtures.listings[0].id,
            websiteId = 1L,
            url = "https://www.realtor.com/property/540909/appartment-for-rent-111",
            active = true,
            imageUrl = "https://picsum.photos/800/600",
        )
    )
}
