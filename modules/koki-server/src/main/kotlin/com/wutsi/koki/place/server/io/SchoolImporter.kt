package com.wutsi.koki.place.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.place.dto.Diploma
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.SchoolLevel
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SchoolImporter(
    placeService: PlaceService,
    locationService: LocationService,
) : AbstractPlaceImporter(placeService, locationService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SchoolImporter::class.java)

        private const val RECORD_NAME = 0
        private const val RECORD_NEIGHBOURHOOD = 1
        private const val RECORD_CITY = 2
        private const val RECORD_PRIVATE = 3
        private const val RECORD_INTERNATIONAL = 4
        private const val RECORD_LEVELS = 5
        private const val RECORD_LANGUAGE = 6
        private const val RECORD_CURRICULUM = 7
        private const val RECORD_DIPLOMAS = 8
        private const val RECORD_WEBSITE_URL = 9
        private const val RECORD_RATING = 10
        private const val RECORD_RATING_SOURCE = 11
        private const val RECORD_LATITUDE = 12
        private const val RECORD_LONGITUDE = 13
    }

    fun import(country: String): ImportResponse {
        var added = 0
        var updated = 0
        val errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/place/refdata/${country.uppercase()}/schools.csv"
        val input = SchoolImporter::class.java.getResourceAsStream(filename)
        if (input == null) {
            errors.add(ImportMessage("", "No schools feed found at $filename"))
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

                val schoolName = record.get(RECORD_NAME)
                val school = findPlace(schoolName, city, PlaceType.SCHOOL)
                if (school == null) {
                    add(record, neighbourhood, city)
                    added++
                } else {
                    update(school, record, neighbourhood, city)
                    updated++
                }
            } catch (ex: Exception) {
                LOGGER.error("Error importing school at row $row", ex)
                errors.add(ImportMessage(row.toString(), "", ex.message))
            } finally {
                row++
            }
        }

        LOGGER.info("${added + updated} school(s) imported with ${errors.size} error(s)")
        return ImportResponse(
            added = added,
            updated = updated,
            errors = errors.size,
            errorMessages = errors
        )
    }

    private fun add(record: CSVRecord, neighbourhood: LocationEntity, city: LocationEntity): PlaceEntity {
        val name = record.get(RECORD_NAME)
        val school = placeService.createSchool(
            name = name,
            neighbourhoodId = neighbourhood.id ?: -1,
            cityId = city.id ?: -1
        )

        // Update school with additional fields from CSV
        school.private = toBoolean(record, RECORD_PRIVATE)
        school.international = toBoolean(record, RECORD_INTERNATIONAL)
        school.levels = toSchoolLevelList(record, RECORD_LEVELS)
        school.languages = toStringList(record, RECORD_LANGUAGE)
        school.academicSystems = toStringList(record, RECORD_CURRICULUM)
        school.diplomas = toDiplomaList(record, RECORD_DIPLOMAS)
        school.websiteUrl = toStringOrNull(record, RECORD_WEBSITE_URL)
        school.rating = toDouble(record, RECORD_RATING)
        // Note: ratingSource (column 11) is not persisted to PlaceEntity
        school.latitude = toDouble(record, RECORD_LATITUDE)
        school.longitude = toDouble(record, RECORD_LONGITUDE)
        return placeService.save(school)
    }

    private fun update(
        school: PlaceEntity,
        record: CSVRecord,
        neighbourhood: LocationEntity,
        city: LocationEntity
    ) {
        school.name = record.get(RECORD_NAME)
        school.neighbourhoodId = neighbourhood.id ?: -1
        school.cityId = city.id ?: -1
        school.private = toBoolean(record, RECORD_PRIVATE)
        school.international = toBoolean(record, RECORD_INTERNATIONAL)
        school.levels = toSchoolLevelList(record, RECORD_LEVELS)
        school.languages = toStringList(record, RECORD_LANGUAGE)
        school.academicSystems = toStringList(record, RECORD_CURRICULUM)
        school.diplomas = toDiplomaList(record, RECORD_DIPLOMAS)
        school.websiteUrl = toStringOrNull(record, RECORD_WEBSITE_URL)
        school.rating = toDouble(record, RECORD_RATING)
        // Note: ratingSource (column 11) is not persisted to PlaceEntity
        school.latitude = toDouble(record, RECORD_LATITUDE)
        school.longitude = toDouble(record, RECORD_LONGITUDE)

        school.status = PlaceStatus.PUBLISHED
        placeService.save(school)
    }

    private fun toSchoolLevelList(record: CSVRecord, column: Int): List<SchoolLevel>? {
        return try {
            val value = record.get(column)?.trim()
            if (value.isNullOrBlank()) {
                null
            } else {
                value.split(";")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .mapNotNull { level ->
                        try {
                            SchoolLevel.valueOf(level)
                        } catch (ex: Exception) {
                            LOGGER.warn("Invalid school level: $level", ex)
                            null
                        }
                    }
                    .takeIf { it.isNotEmpty() }
            }
        } catch (ex: Exception) {
            null
        }
    }

    private fun toDiplomaList(record: CSVRecord, column: Int): List<Diploma>? {
        return try {
            val value = record.get(column)?.trim()
            if (value.isNullOrBlank()) {
                null
            } else {
                value.split(";")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .mapNotNull { diploma ->
                        try {
                            Diploma.valueOf(diploma)
                        } catch (ex: Exception) {
                            LOGGER.warn("Invalid diploma: $diploma", ex)
                            null
                        }
                    }
                    .takeIf { it.isNotEmpty() }
            }
        } catch (ex: Exception) {
            null
        }
    }
}
