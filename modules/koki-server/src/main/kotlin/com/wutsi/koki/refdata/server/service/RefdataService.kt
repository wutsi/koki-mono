package com.wutsi.koki.refdata.server.service

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
        }
    }
}
