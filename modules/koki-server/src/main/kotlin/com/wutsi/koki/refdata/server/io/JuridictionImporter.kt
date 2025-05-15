package com.wutsi.koki.refdata.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.JuridictionEntity
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.JuridictionService
import com.wutsi.koki.refdata.server.service.LocationService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class JuridictionImporter(
    private val service: JuridictionService,
    private val locationService: LocationService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(JuridictionImporter::class.java)

        private const val RECORD_ID = 0
        private const val RECORD_STATE = 1
    }

    fun import(country: String): ImportResponse {
        var added = 0
        var updated = 0
        var errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/refdata/juridiction/$country.csv"
        val input = JuridictionImporter::class.java.getResourceAsStream(filename) ?: throw ConflictException(
            error = Error(
                code = ErrorCode.JURIDICTION_COUNTRY_NOT_SUPPORTED,
                message = "Resource not found $filename",
            )
        )

        val states = locationService.search(country = country, type = LocationType.STATE, limit = Integer.MAX_VALUE)
            .associateBy { state -> state.asciiName.uppercase() }

        val parser = createParser(input)
        for (record in parser) {
            if (record.recordNumber == 1L) {
                continue
            }
            val id = record.get(RECORD_ID).toLong()
            val stateName = record.get(RECORD_STATE)?.trim()?.ifEmpty { null }
            val state = stateName?.let { data -> states[data.uppercase()] }
            if (state == null && stateName != null) {
                errors.add(
                    ImportMessage(
                        location = record.recordNumber.toString(),
                        code = ErrorCode.LOCATION_NOT_FOUND,
                        message = "State not found: $stateName",
                    )
                )
            } else {
                var juridiction = service.getByIdOrNull(id)
                if (juridiction == null) {
                    create(country, state, record)
                    added++
                } else {
                    update(juridiction, country, state)
                    updated++
                }
            }
        }

        LOGGER.info("${added + updated} juridiction(s) for $country imported with ${errors.size} error(s)")
        return ImportResponse(
            added = added, updated = updated, errors = errors.size, errorMessages = errors
        )
    }

    private fun createParser(input: InputStream): CSVParser {
        return CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create().setDelimiter(",").get(),
        )
    }

    private fun create(country: String, state: LocationEntity?, record: CSVRecord): JuridictionEntity {
        return service.save(
            JuridictionEntity(
                id = record.get(RECORD_ID).toLong(),
                country = country,
                stateId = state?.id,
            )
        )
    }

    private fun update(juridiction: JuridictionEntity, country: String, state: LocationEntity?) {
        juridiction.country = country
        juridiction.stateId = state?.id
        service.save(juridiction)
    }
}
