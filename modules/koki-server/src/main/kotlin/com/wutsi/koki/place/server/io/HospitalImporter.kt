package com.wutsi.koki.place.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HospitalImporter(
    placeService: PlaceService,
    locationService: LocationService,
) : AbstractPlaceImporter(placeService, locationService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(HospitalImporter::class.java)

        private const val RECORD_NAME = 0
        private const val RECORD_NEIGHBOURHOOD = 1
        private const val RECORD_CITY = 2
        private const val RECORD_PRIVATE = 3
        private const val RECORD_INTERNATIONAL = 4
        private const val RECORD_WEBSITE_URL = 5
        private const val RECORD_RATING = 6
        private const val RECORD_RATING_SOURCE = 7
        private const val RECORD_LATITUDE = 8
        private const val RECORD_LONGITUDE = 9
    }

    fun import(country: String): ImportResponse {
        var added = 0
        var updated = 0
        val errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/place/refdata/${country.uppercase()}/hospitals.csv"
        val input = HospitalImporter::class.java.getResourceAsStream(filename)
        if (input == null) {
            errors.add(ImportMessage("", "No hospitals feed found at $filename"))
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

                val hospitalName = record.get(RECORD_NAME)
                val hospital = findPlace(hospitalName, city, PlaceType.HOSPITAL)
                if (hospital == null) {
                    add(record, neighbourhood, city)
                    added++
                } else {
                    update(hospital, record, neighbourhood, city)
                    updated++
                }
            } catch (ex: Exception) {
                LOGGER.error("Error importing hospital at row $row", ex)
                errors.add(ImportMessage(row.toString(), "", ex.message))
            } finally {
                row++
            }
        }

        LOGGER.info("${added + updated} hospital(s) imported with ${errors.size} error(s)")
        return ImportResponse(
            added = added,
            updated = updated,
            errors = errors.size,
            errorMessages = errors
        )
    }

    private fun add(record: CSVRecord, neighbourhood: LocationEntity, city: LocationEntity): PlaceEntity {
        val name = record.get(RECORD_NAME)
        val hospital = placeService.createPlace(
            name = name,
            type = PlaceType.HOSPITAL,
            neighbourhoodId = neighbourhood.id ?: -1,
            cityId = city.id ?: -1
        )

        // Update hospital with additional fields from CSV
        hospital.private = toBoolean(record, RECORD_PRIVATE)
        hospital.international = toBoolean(record, RECORD_INTERNATIONAL)
        hospital.websiteUrl = toStringOrNull(record, RECORD_WEBSITE_URL)
        hospital.rating = toDouble(record, RECORD_RATING)
        hospital.latitude = toDouble(record, RECORD_LATITUDE)
        hospital.longitude = toDouble(record, RECORD_LONGITUDE)
        // Note: ratingSource (column 7) is not persisted to PlaceEntity

        return placeService.save(hospital)
    }

    private fun update(
        hospital: PlaceEntity,
        record: CSVRecord,
        neighbourhood: LocationEntity,
        city: LocationEntity
    ) {
        hospital.name = record.get(RECORD_NAME)
        hospital.neighbourhoodId = neighbourhood.id ?: -1
        hospital.cityId = city.id ?: -1
        hospital.private = toBoolean(record, RECORD_PRIVATE)
        hospital.international = toBoolean(record, RECORD_INTERNATIONAL)
        hospital.websiteUrl = toStringOrNull(record, RECORD_WEBSITE_URL)
        hospital.rating = toDouble(record, RECORD_RATING)
        hospital.latitude = toDouble(record, RECORD_LATITUDE)
        hospital.longitude = toDouble(record, RECORD_LONGITUDE)
        // Note: ratingSource (column 7) is not persisted to PlaceEntity
        hospital.status = PlaceStatus.PUBLISHED
        placeService.save(hospital)
    }
}
