package com.wutsi.koki.chatbot.telegram

import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType

object RefDataFixtures {
    // Location
    val locations = listOf(
        Location(id = 100, type = LocationType.STATE, name = "Quebec", country = "CA"),
        Location(id = 200, type = LocationType.STATE, name = "Ontario", country = "CA"),
        Location(
            id = 110,
            type = LocationType.CITY,
            name = "Montreal",
            parentId = 100,
            country = "CA",
            latitude = 45.508888,
            longitude = -73.561668
        ),
        Location(
            id = 120,
            type = LocationType.CITY,
            name = "Saint Isidore",
            parentId = 100,
            country = "CA",
            latitude = 45.300102,
            longitude = -73.680274
        ),
        Location(
            id = 210,
            type = LocationType.CITY,
            name = "Toronto",
            parentId = 200,
            country = "CA",
            latitude = 43.651070,
            longitude = -79.347015
        ),
        Location(
            id = 111,
            type = LocationType.NEIGHBORHOOD,
            name = "Centre-Ville",
            parentId = 110,
            country = "CA",
            latitude = 45.508888,
            longitude = -73.561668
        ),
        Location(
            id = 112,
            type = LocationType.NEIGHBORHOOD,
            name = "Ahunsic",
            parentId = 110,
            country = "CA",
            latitude = 45.5330,
            longitude = -73.7170
        ),
        Location(
            id = 113,
            type = LocationType.NEIGHBORHOOD,
            name = "Mont-Royal",
            parentId = 110,
            country = "CA",
            latitude = 45.516109,
            longitude = -73.643059
        ),
        Location(id = 114, type = LocationType.NEIGHBORHOOD, name = "Cote des Neiges", parentId = 110, country = "CA"),
    )
    val cities = listOf(
        Location(
            id = 110,
            type = LocationType.CITY,
            name = "Montreal",
            parentId = 100,
            country = "CA",
            latitude = 45.508888,
            longitude = -73.561668,
        ),
        Location(
            id = 120,
            type = LocationType.CITY,
            name = "Saint Isidore",
            parentId = 100,
            country = "CA",
            latitude = 45.30078966757618,
            longitude = -73.68129885390888,
        ),
        Location(id = 210, type = LocationType.CITY, name = "Toronto", parentId = 200, country = "CA"),
    )
    val neighborhoods = listOf(
        Location(
            id = 111,
            type = LocationType.NEIGHBORHOOD,
            name = "Centre-Ville",
            parentId = 110,
            country = "CA",
            latitude = 45.555979,
            longitude = -73.662675
        ),
        Location(
            id = 112,
            type = LocationType.NEIGHBORHOOD,
            name = "Ahunsic",
            parentId = 110,
            country = "CA",
            latitude = 45.548849,
            longitude = -73.704903
        ),
        Location(id = 113, type = LocationType.NEIGHBORHOOD, name = "Mont-Royal", parentId = 110, country = "CA"),
        Location(id = 114, type = LocationType.NEIGHBORHOOD, name = "Cote des Neiges", parentId = 110, country = "CA"),
    )

    val countries = listOf(
        Location(
            id = 888888,
            type = LocationType.COUNTRY,
            name = "Canada",
            parentId = null,
            country = "CA",
        ),
    )
}
