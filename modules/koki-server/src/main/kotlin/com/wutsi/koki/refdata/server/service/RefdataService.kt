package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.place.server.io.HospitalImporter
import com.wutsi.koki.place.server.io.MarketImporter
import com.wutsi.koki.place.server.io.SchoolImporter
import com.wutsi.koki.place.server.io.ToDoImporter
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.server.io.AmenityImporter
import com.wutsi.koki.refdata.server.io.CategoryImporter
import com.wutsi.koki.refdata.server.io.GeonamesImporter
import com.wutsi.koki.refdata.server.io.NeighbourhoodImporter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RefdataService(
    private val categoryImporter: CategoryImporter,
    private val amenityImporter: AmenityImporter,
    private val geonamesImporter: GeonamesImporter,
    private val neighbourhoodImporter: NeighbourhoodImporter,
    private val schoolImporter: SchoolImporter,
    private val hospitalImporter: HospitalImporter,
    private val marketImporter: MarketImporter,
    private val toDoImporter: ToDoImporter
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RefdataService::class.java)

        val COUNTRIES = listOf(
            "CM", "CA"
        )
    }

    fun import() {
        LOGGER.info("Initializing")

        importCategories()
        importAmenities()
        importCountryData()

        LOGGER.info("Initialized")
    }

    private fun importCategories() {
        CategoryType.entries.forEach { type ->
            if (type != CategoryType.UNKNOWN) {
                LOGGER.info("$type - Loading categories")
                categoryImporter.import(type)
            }
        }
    }

    private fun importAmenities() {
        amenityImporter.import()
    }

    private fun importCountryData() {
        COUNTRIES.forEach { country ->
            LOGGER.info("$country - Loading locations")
            geonamesImporter.import(country)

            LOGGER.info("$country - Loading neighborhoods")
            neighbourhoodImporter.import(country)

            LOGGER.info("$country - Loading schools")
            schoolImporter.import(country)

            LOGGER.info("$country - Loading hospitals")
            hospitalImporter.import(country)

            LOGGER.info("$country - Loading markets")
            marketImporter.import(country)

            LOGGER.info("$country - Loading todos")
            toDoImporter.import(country)
        }
    }
}
