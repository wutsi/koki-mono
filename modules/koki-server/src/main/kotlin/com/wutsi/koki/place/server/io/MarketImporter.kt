package com.wutsi.koki.place.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import jakarta.transaction.Transactional
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MarketImporter(
    placeService: PlaceService,
    locationService: LocationService,
) : AbstractPlaceImporter(placeService, locationService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MarketImporter::class.java)

        private const val RECORD_NAME = 0
        private const val RECORD_NEIGHBOURHOOD = 1
        private const val RECORD_CITY = 2
        private const val RECORD_TYPE = 3
        private const val RECORD_INTERNATIONAL = 4
        private const val RECORD_WEBSITE_URL = 5
        private const val RECORD_RATING = 6
        private const val RECORD_RATING_SOURCE = 7
        private const val RECORD_LATITUDE = 8
        private const val RECORD_LONGITUDE = 9
    }

    @Transactional
    fun import(country: String): ImportResponse {
        var added = 0
        var updated = 0
        val errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/place/refdata/${country.uppercase()}/markets.csv"
        val input = MarketImporter::class.java.getResourceAsStream(filename)
        if (input == null) {
            errors.add(ImportMessage("", "No markets feed found at $filename"))
            return ImportResponse(
                errors = errors.size,
                errorMessages = errors
            )
        }

        var row = 0
        val parser = createParser(input)
        for (record in parser) {
            try {
                if (record.recordNumber == 1L) {
                    continue
                }

                val cityName = record.get(RECORD_CITY)
                val city = findCity(cityName, country)
                if (city == null) {
                    errors.add(ImportMessage(row.toString(), "City not found: $cityName"))
                    continue
                }

                val neighbourhoodName = record.get(RECORD_NEIGHBOURHOOD)
                val neighbourhood = findNeighbourhood(neighbourhoodName, city)
                if (neighbourhood == null) {
                    errors.add(
                        ImportMessage(
                            row.toString(),
                            "Neighbourhood not found: $neighbourhoodName in $cityName"
                        )
                    )
                    continue
                }

                val marketName = record.get(RECORD_NAME)
                val marketType = toPlaceType(record, RECORD_TYPE)
                val market = findPlace(marketName, city, marketType)
                if (market == null) {
                    add(record, neighbourhood, city, marketType)
                    added++
                } else {
                    update(market, record, neighbourhood, city, marketType)
                    updated++
                }
            } catch (ex: Exception) {
                LOGGER.error("Error importing market at row $row", ex)
                errors.add(ImportMessage(row.toString(), "", ex.message))
            } finally {
                row++
            }
        }

        LOGGER.info("${added + updated} market(s) imported with ${errors.size} error(s)")
        return ImportResponse(
            added = added,
            updated = updated,
            errors = errors.size,
            errorMessages = errors
        )
    }

    private fun add(
        record: CSVRecord,
        neighbourhood: LocationEntity,
        city: LocationEntity,
        marketType: PlaceType
    ): PlaceEntity {
        val name = record.get(RECORD_NAME)
        val market = placeService.createPlace(
            name = name,
            type = marketType,
            neighbourhoodId = neighbourhood.id ?: -1,
            cityId = city.id ?: -1
        )

        // Update market with additional fields from CSV
        market.international = toBoolean(record, RECORD_INTERNATIONAL)
        market.websiteUrl = toStringOrNull(record, RECORD_WEBSITE_URL)
        market.rating = toDouble(record, RECORD_RATING)
        market.latitude = toDouble(record, RECORD_LATITUDE)
        market.longitude = toDouble(record, RECORD_LONGITUDE)
        // Note: ratingSource (column 7) is not persisted to PlaceEntity

        return placeService.save(market)
    }

    private fun update(
        market: PlaceEntity,
        record: CSVRecord,
        neighbourhood: LocationEntity,
        city: LocationEntity,
        marketType: PlaceType
    ) {
        market.name = record.get(RECORD_NAME)
        market.neighbourhoodId = neighbourhood.id ?: -1
        market.cityId = city.id ?: -1
        market.international = toBoolean(record, RECORD_INTERNATIONAL)
        market.websiteUrl = toStringOrNull(record, RECORD_WEBSITE_URL)
        market.rating = toDouble(record, RECORD_RATING)
        market.latitude = toDouble(record, RECORD_LATITUDE)
        market.longitude = toDouble(record, RECORD_LONGITUDE)
        // Note: ratingSource (column 7) is not persisted to PlaceEntity
        market.status = PlaceStatus.PUBLISHED
        placeService.save(market)
    }
}
