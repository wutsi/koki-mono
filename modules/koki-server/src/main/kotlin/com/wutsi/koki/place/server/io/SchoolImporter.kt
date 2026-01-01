package com.wutsi.koki.place.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.place.dto.Diploma
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.SchoolLevel
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class SchoolImporter(
    private val placeService: PlaceService,
    private val locationService: LocationService,
) {
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
    }

    fun import(): ImportResponse {
        var added = 0
        var updated = 0
        val errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/place/refdata/schools.csv"
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
                val city = findCity(cityName)
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
                val school = findSchool(schoolName, city)
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

    private fun createParser(input: InputStream): CSVParser {
        return CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .get(),
        )
    }

    private fun findCity(cityName: String): LocationEntity? {
        val cities = locationService.search(
            keyword = cityName,
            types = listOf(LocationType.CITY),
            limit = 1
        )
        return cities.firstOrNull { city ->
            toAscii(city.name).equals(toAscii(cityName), ignoreCase = true)
        }
    }

    private fun findNeighbourhood(neighbourhoodName: String, city: LocationEntity): LocationEntity? {
        val neighbourhoods = locationService.search(
            keyword = neighbourhoodName,
            parentId = city.id,
            types = listOf(LocationType.NEIGHBORHOOD),
            limit = 10
        )
        return neighbourhoods.firstOrNull { neighbourhood ->
            toAscii(neighbourhood.name).equals(toAscii(neighbourhoodName), ignoreCase = true)
        }
    }

    private fun findSchool(schoolName: String, city: LocationEntity): PlaceEntity? {
        return placeService.findSchool(schoolName, city.id ?: -1)
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

        return placeService.save(school)
    }

    private fun update(
        school: PlaceEntity,
        record: CSVRecord,
        neighbourhood: LocationEntity,
        city: LocationEntity
    ) {
        school.name = record.get(RECORD_NAME)
        school.asciiName = toAscii(school.name)
        school.neighbourhoodId = neighbourhood.id ?: -1
        school.cityId = city.id ?: -1
        school.private = toBoolean(record, RECORD_PRIVATE)
        school.international = toBoolean(record, RECORD_INTERNATIONAL)
        school.levels = toSchoolLevelList(record, RECORD_LEVELS)
        school.languages = toStringList(record, RECORD_LANGUAGE)
        school.academicSystems = toStringList(record, RECORD_CURRICULUM)
        school.diplomas = toDiplomaList(record, RECORD_DIPLOMAS)
        school.websiteUrl = toStringOrNull(record, RECORD_WEBSITE_URL)
        school.status = PlaceStatus.PUBLISHED
        placeService.save(school)
    }

    private fun toAscii(str: String): String {
        return StringUtils.toAscii(str).lowercase()
    }

    private fun toBoolean(record: CSVRecord, column: Int): Boolean? {
        return try {
            val value = record.get(column)?.trim()?.lowercase()
            when (value) {
                "true", "1", "yes" -> true
                "false", "0", "no" -> false
                else -> null
            }
        } catch (ex: Exception) {
            null
        }
    }

    private fun toStringOrNull(record: CSVRecord, column: Int): String? {
        return try {
            val value = record.get(column)?.trim()
            if (value.isNullOrBlank()) null else value
        } catch (ex: Exception) {
            null
        }
    }

    private fun toStringList(record: CSVRecord, column: Int): List<String>? {
        return try {
            val value = record.get(column)?.trim()
            if (value.isNullOrBlank()) {
                null
            } else {
                value.split(";")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .takeIf { it.isNotEmpty() }
            }
        } catch (ex: Exception) {
            null
        }
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
