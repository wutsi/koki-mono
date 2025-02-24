package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.server.io.CategoryImporter
import com.wutsi.koki.refdata.server.io.GeonamesImporter
import com.wutsi.koki.refdata.server.io.JuridictionImporter
import com.wutsi.koki.refdata.server.io.SalesTaxImporter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RefdataService(
    private val categoryImporter: CategoryImporter,
    private val juridictionImporter: JuridictionImporter,
    private val geonamesImporter: GeonamesImporter,
    private val salesTaxImporter: SalesTaxImporter,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RefdataService::class.java)

        val COUNTRIES = listOf(
            "CM", "CA"
        )
    }

    fun load() {
        LOGGER.info("Initializing")

        loadCategories()
        loadCountryData()

        LOGGER.info("Initialized")
    }

    private fun loadCategories() {
        CategoryType.entries.forEach { type ->
            if (type != CategoryType.UNKNOWN) {
                LOGGER.info("$type - Loading categories")
                categoryImporter.import(type)
            }
        }
    }

    private fun loadCountryData() {
        COUNTRIES.forEach { country ->
            LOGGER.info("$country - Loading locations")
            geonamesImporter.import(country)

            LOGGER.info("$country - Loading juridiction")
            juridictionImporter.import(country)

            LOGGER.info("$country - Loading Sales Tax")
            salesTaxImporter.import(country)
        }
    }
}
