package com.wutsi.koki

import com.wutsi.koki.refdata.dto.Category
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.Unit

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
        Location(id = 110, type = LocationType.CITY, name = "Montreal", parentId = 100, country = "CA"),
        Location(id = 120, type = LocationType.CITY, name = "Saint Isidore", parentId = 100, country = "CA"),
        Location(id = 210, type = LocationType.CITY, name = "Toronto", parentId = 200, country = "CA"),
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
}
