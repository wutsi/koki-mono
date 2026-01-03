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
class ToDoImporter(
    placeService: PlaceService,
    locationService: LocationService,
) : AbstractPlaceImporter(placeService, locationService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ToDoImporter::class.java)

        private const val RECORD_NAME = 0
        private const val RECORD_TYPE = 1
        private const val RECORD_NEIGHBOURHOOD = 2
        private const val RECORD_CITY = 3
        private const val RECORD_WEBSITE_URL = 4
        private const val RECORD_LATITUDE = 5
        private const val RECORD_LONGITUDE = 6
        private const val RECORD_RATING = 7
        private const val RECORD_RATING_SOURCE = 8
    }

    @Transactional
    fun import(country: String): ImportResponse {
        var added = 0
        var updated = 0
        val errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/place/refdata/${country.uppercase()}/todos.csv"
        val input = ToDoImporter::class.java.getResourceAsStream(filename)
        if (input == null) {
            errors.add(ImportMessage("", "No todo feed found at $filename"))
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

                val name = record.get(RECORD_NAME)
                val type = toPlaceType(record, RECORD_TYPE)
                val todo = findPlace(name, city, type)
                if (todo == null) {
                    add(record, neighbourhood, city, type)
                    added++
                } else {
                    update(todo, record, neighbourhood, city)
                    updated++
                }
            } catch (ex: Exception) {
                LOGGER.error("Error importing todo at row $row", ex)
                errors.add(ImportMessage(row.toString(), "", ex.message))
            } finally {
                row++
            }
        }

        LOGGER.info("${added + updated} todo(s) imported with ${errors.size} error(s)")
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
        type: PlaceType
    ): PlaceEntity {
        val name = record.get(RECORD_NAME)
        val todo = placeService.createPlace(
            name = name,
            type = type,
            neighbourhoodId = neighbourhood.id ?: -1,
            cityId = city.id ?: -1
        )

        // Update todo with additional fields from CSV
        todo.websiteUrl = toStringOrNull(record, RECORD_WEBSITE_URL)
        todo.rating = toDouble(record, RECORD_RATING)
        todo.latitude = toDouble(record, RECORD_LATITUDE)
        todo.longitude = toDouble(record, RECORD_LONGITUDE)
        // Note: ratingSource (column 8) is not persisted to PlaceEntity

        return placeService.save(todo)
    }

    private fun update(
        todo: PlaceEntity,
        record: CSVRecord,
        neighbourhood: LocationEntity,
        city: LocationEntity,
    ) {
        todo.name = record.get(RECORD_NAME)
        todo.neighbourhoodId = neighbourhood.id ?: -1
        todo.cityId = city.id ?: -1
        todo.websiteUrl = toStringOrNull(record, RECORD_WEBSITE_URL)
        todo.rating = toDouble(record, RECORD_RATING)
        todo.latitude = toDouble(record, RECORD_LATITUDE)
        todo.longitude = toDouble(record, RECORD_LONGITUDE)
        // Note: ratingSource (column 8) is not persisted to PlaceEntity
        todo.status = PlaceStatus.PUBLISHED
        placeService.save(todo)
    }
}
