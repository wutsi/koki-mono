package com.wutsi.koki.portal.pub

import com.wutsi.koki.refdata.dto.Amenity
import com.wutsi.koki.refdata.dto.Category
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import kotlin.collections.flatMap

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

    // Categories
    val categories = listOf(
        Category(id = 1000, type = CategoryType.SERVICE, name = "Automotive", longName = "Automotive"),
        Category(id = 1001, type = CategoryType.SERVICE, name = "Repair", longName = "Automotive > Repair"),
        Category(
            id = 1002,
            type = CategoryType.SERVICE,
            name = "Engine Repair",
            longName = "Automotive > Repair > Engine Repair"
        ),
        Category(
            id = 1002,
            type = CategoryType.SERVICE,
            name = "Brake Repair",
            longName = "Automotive > Repair > Brake Repair"
        ),
    )

    // Amenities
    private var amenityIdCounter = System.currentTimeMillis()
    val amenities = categories.flatMap { category ->
        listOf(
            Amenity(
                id = amenityIdCounter++,
                categoryId = category.id,
                name = "Electricity",
                top = false,
            ),
            Amenity(
                id = amenityIdCounter++,
                categoryId = category.id,
                name = "Prepaid meter",
                top = true
            ),
            Amenity(
                id = amenityIdCounter++,
                categoryId = categories[1].id,
                name = "Ocean view",
                top = false
            ),
            Amenity(
                id = amenityIdCounter++,
                categoryId = categories[1].id,
                name = "Beach access",
                top = true
            ),
            Amenity(
                id = amenityIdCounter++,
                categoryId = categories[2].id,
                name = "Security Guard",
                top = true
            ),
            Amenity(
                id = amenityIdCounter++,
                categoryId = categories[2].id,
                name = "Camera",
                top = false
            ),
        )
    }
}
