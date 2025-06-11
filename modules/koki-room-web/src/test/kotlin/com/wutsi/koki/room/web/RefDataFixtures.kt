package com.wutsi.koki.room.web

import com.wutsi.koki.refdata.dto.Amenity
import com.wutsi.koki.refdata.dto.Category
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.dto.Juridiction
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.SalesTax
import com.wutsi.koki.refdata.dto.Unit
import org.openqa.selenium.By.id

object RefDataFixtures {
    // Units
    val units = listOf(
        Unit(id = 100, name = "Hours"),
        Unit(id = 110, name = "Days"),
        Unit(id = 120, name = "Month"),
        Unit(id = 130, name = "Visit"),
    )

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

    // Juridiction
    val juridictions = listOf(
        Juridiction(id = 100, stateId = null, country = "CA"),
        Juridiction(id = 101, stateId = locations[0].id, country = "CA"),
        Juridiction(id = 102, stateId = locations[1].id, country = "CA"),
        Juridiction(id = 237, stateId = null, country = "CM"),
    )

    // Sales Taxes
    val salesTaxes = listOf(
        SalesTax(id = 10000, name = "GST", rate = 5.0, juridictionId = 100),
        SalesTax(id = 10100, name = "GST", rate = 5.0, juridictionId = 101),
        SalesTax(id = 10101, name = "PST", rate = 9.975, juridictionId = 101),
        SalesTax(id = 10200, name = "HST", rate = 13.0, juridictionId = 102),
    )

    // Amenities
    private var amenityIdCounter = System.currentTimeMillis()
    val amenities = categories.flatMap { category ->
        listOf(
            Amenity(
                id = amenityIdCounter++,
                categoryId = category.id,
                name = "Amenity #$amenityIdCounter",
            ),
            Amenity(
                id = amenityIdCounter++,
                categoryId = category.id,
                name = "Amenity #$amenityIdCounter",
            ),
            Amenity(
                id = amenityIdCounter++,
                categoryId = category.id,
                name = "Amenity #$amenityIdCounter",
            ),
        )
    }
}
