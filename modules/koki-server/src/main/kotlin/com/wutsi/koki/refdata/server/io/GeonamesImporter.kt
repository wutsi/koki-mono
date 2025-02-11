package com.wutsi.koki.refdata.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.apache.tools.zip.ZipFile
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.text.Normalizer
import java.util.Locale
import kotlin.io.outputStream

@Service
class LocationTSVImporter(private val service: LocationService) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LocationTSVImporter::class.java)

        private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        private val MIN_POPULATION = 1000
        private const val RECORD_ID = 0
        private const val RECORD_NAME = 1
        private const val RECORD_LATITUDE = 4
        private const val RECORD_LONGITUDE = 5
        private const val RECORD_FEATURE_CLASS = 6
        private const val RECORD_FEATURE_CODE = 7
        private const val RECORD_COUNTRY = 8
        private const val RECORD_ADMIN1 = 11
        private const val RECORD_POPULATION = 14
        private const val RECORD_TIMEZONE = 17
    }

    fun import(country: String): ImportResponse {
        var row = 0
        var added = 0
        var updated = 0
        var errors = mutableListOf<ImportMessage>()

        val url = URL("https://download.geonames.org/export/dump/${country.uppercase()}.zip")
        val file = download(url, "$country.txt")
        val parser = createParser(file)
        for (record in parser) {
            if (accept(record, country)) {
                try {
                    val id = record.get(RECORD_ID).toLong()
                    var location = service.getOrNull(id)
                    if (location == null) {
                        location = add(id, record)
                        added++
                        if (LOGGER.isDebugEnabled) {
                            LOGGER.debug("$row - Added: ${location.name} - ${location.type}")
                        }
                    } else {
                        update(location, record)
                        updated++
                        if (LOGGER.isDebugEnabled) {
                            LOGGER.debug("$row - Updated: ${location.name} - ${location.type}")
                        }
                    }
                } catch (ex: Exception) {
                    errors.add(ImportMessage(row.toString(), "", ex.message))
                } finally {
                    row++
                }
            }
        }
        return ImportResponse(
            added = added,
            updated = updated,
            errors = errors.size,
            errorMessages = errors
        )
    }

    fun importStates(country: String): Map<String, Long> {
        val result = mutableMapOf<String, Long> ()
        val url = URL("https://download.geonames.org/export/dump/admin1CodesASCII.txt")
        val file = downloadAdminCode1(url)
        val parser = createParser(file)
        for (record in parser) {
            val code = record.get(0)
            val stateId = record.get(3).toLong()
            if (code.startsWith(country)) {
                result[code] = stateId
            }
        }

        LOGGER.info(" ${result.size} states loaded")
        return result
    }

    fun downloadAdminCode1(url: URL): File {
        LOGGER.info("Downloading $url")

        val file = Files.createTempFile("admin1CodesASCII", ".txt").toFile()
        url.openStream().use { input ->
            val output = FileOutputStream(file)
            output.use {
                input.copyTo(output)
            }
        }
        return file
    }

    private fun createParser(file: File): CSVParser {
        return CSVParser.parse(
            file,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter("\t")
                .get(),
        )
    }

    private fun add(id: Long, record: CSVRecord): LocationEntity {
        val type = toLocationType(record)
        val name = toLocationName(record, type)

        return service.save(
            LocationEntity(
                id = id,
                name = name,
                asciiName = toAscii(name),
                country = record.get(RECORD_COUNTRY),
                type = type,
                population = record.get(RECORD_POPULATION).toLong()
            )
        )
    }

    private fun update(location: LocationEntity, record: CSVRecord) {
        val type = toLocationType(record)
        val name = toLocationName(record, type)

        location.name = name
        location.asciiName = toAscii(name)
        location.country = record.get(RECORD_COUNTRY)
        location.type = type
        location.population = record.get(RECORD_POPULATION).toLong()
        service.save(location)
    }

    private fun toLocationName(record: CSVRecord, type: LocationType): String {
        return if (type == LocationType.COUNTRY) {
            Locale("en", record.get(RECORD_COUNTRY)).displayCountry
        } else {
            record.get(RECORD_NAME)
        }
    }

    private fun toLocationType(record: CSVRecord): LocationType {
        val code = record.get(RECORD_FEATURE_CODE)
        return if (record.get(RECORD_FEATURE_CLASS) == "P") {
            LocationType.CITY
        } else if (record.get(RECORD_FEATURE_CLASS) == "A") {
            if (code == "ADM1") {
                LocationType.STATE
            } else if (code == "PCLI") {
                LocationType.COUNTRY
            } else {
                LocationType.UNKNOWN
            }
        } else {
            LocationType.UNKNOWN
        }
    }

    private fun accept(record: CSVRecord, country: String): Boolean {
        return country ==
            record.get(RECORD_COUNTRY) &&
            ( // Cities
                record.get(RECORD_FEATURE_CLASS) == "P" &&
                    listOf(
                        "PPL",
                        "PPL2",
                        "PPL3",
                        "PPL4",
                        "PPLC",
                        "PPLCH",
                    ).contains(record.get(RECORD_FEATURE_CODE)) &&
                    !record.get(RECORD_POPULATION).isNullOrEmpty() &&
                    record.get(RECORD_POPULATION).toLong() >= MIN_POPULATION
                ) ||
            ( // State
                record.get(RECORD_FEATURE_CLASS) == "A" &&
                    (record.get(RECORD_FEATURE_CODE) == "ADM1" || record.get(RECORD_FEATURE_CODE) == "PCLI"))
    }

    private fun download(url: URL, filename: String): File {
        LOGGER.info("Downloading $url")

        val zip = Files.createTempFile("file", ".zip")
        url.openStream().use { input ->
            val output = FileOutputStream(zip.toFile())
            output.use {
                input.copyTo(output)
            }
        }

        // Extract
        ZipFile(zip.toFile()).use { zip ->
            val entry = zip.entries.asSequence().find { it.name == filename }
            if (entry != null) {
                val file = Files.createTempFile("file", ".txt").toFile()
                zip.getInputStream(entry).use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                    return file
                }
            }
        }

        throw NotFoundException(
            error = Error(
                code = ErrorCode.LOCATION_FEED_NOT_FOUND,
                data = mapOf("url" to url.toString())
            )
        )
    }

    fun toAscii(str: String): String {
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }
}
