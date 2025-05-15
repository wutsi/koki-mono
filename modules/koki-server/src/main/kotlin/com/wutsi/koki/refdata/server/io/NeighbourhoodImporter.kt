package com.wutsi.koki.refdata.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream
import java.text.Normalizer

@Service
class NeighbourhoodImporter(
    private val locationService: LocationService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(JuridictionImporter::class.java)

        private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        private const val RECORD_ID = 0
        private const val RECORD_NAME = 1
        private const val RECORD_CITY = 2
    }

    fun import(country: String): ImportResponse {
        var added = 0
        var updated = 0
        var errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/refdata/neighbourhood/$country.csv"
        val input = JuridictionImporter::class.java.getResourceAsStream(filename)
        if (input == null) {
            errors.add(ImportMessage("", "No neighbourhood feed found for $country"))
            return ImportResponse(
                errors = errors.size,
                errorMessages = errors
            )
        }

        var row = 0
        val cities = locationService
            .search(country = country, type = LocationType.CITY, limit = Integer.MAX_VALUE)
            .associateBy { city -> city.asciiName.lowercase() }

        val parser = createParser(input)
        for (record in parser) {
            try {
                if (record.recordNumber == 1L) {
                    continue
                }
                val id = record.get(RECORD_ID).toLong()
                val cityName = record.get(RECORD_CITY)
                val city = cities[toAscii(cityName.lowercase())]
                if (city == null) {
                    errors.add(ImportMessage(row.toString(), "Invalid city: $city"))
                    continue
                }

                var location = locationService.getOrNull(id)
                if (location == null) {
                    add(record, city)
                    added++
                } else {
                    if (location.type == LocationType.NEIGHBORHOOD) {
                        update(location, record, city)
                        updated++
                    } else {
                        errors.add(
                            ImportMessage(
                                row.toString(),
                                "The neighbourhood id<$id> associated to another location"
                            )
                        )
                    }
                }
            } catch (ex: Exception) {
                errors.add(ImportMessage(row.toString(), "", ex.message))
            } finally {
                row++
            }
        }

        LOGGER.info("${added + updated} neighborhood(s) for $country imported with ${errors.size} error(s)")
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

    private fun add(record: CSVRecord, city: LocationEntity): LocationEntity {
        return locationService.save(
            LocationEntity(
                id = record.get(RECORD_ID).toLong(),
                name = record.get(RECORD_NAME),
                asciiName = toAscii(record.get(RECORD_NAME)),
                parentId = city.id,
                type = LocationType.NEIGHBORHOOD,
                country = city.country,
            )
        )
    }

    private fun update(neighbourhood: LocationEntity, record: CSVRecord, city: LocationEntity) {
        neighbourhood.name = record.get(RECORD_NAME)
        neighbourhood.asciiName = toAscii(record.get(RECORD_NAME))
        neighbourhood.parentId = city.id
        neighbourhood.country = city.country
        locationService.save(neighbourhood)
    }

    private fun toAscii(str: String): String {
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }
}
