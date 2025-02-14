package com.wutsi.koki.refdata.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.refdata.server.service.SalesTaxService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class SalesTaxImporter(
    private val service: SalesTaxService,
    private val locationService: LocationService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SalesTaxImporter::class.java)

        private const val RECORD_NAME = 0
        private const val RECORD_STATE = 1
        private const val RECORD_RATE = 2
        private const val RECORD_PRIORITY = 3
    }

    fun import(country: String): ImportResponse {
        var added = 0
        var updated = 0
        var errors = mutableListOf<ImportMessage>()

        /* import */
        val input = SalesTaxImporter::class.java.getResourceAsStream("/refdata/sales-tax//$country.csv")
            ?: throw ConflictException(error = Error(code = ErrorCode.SALES_TAX_COUNTRY_NOT_SUPPORTED))

        val salesTaxes = service.getByCountry(country).toMutableList()
        val states = locationService.search(country = country, type = LocationType.STATE)
            .associateBy { state -> state.asciiName.uppercase() }

        val salesTaxIds = mutableListOf<Long>()
        val parser = createParser(input)
        for (record in parser) {
            if (record.recordNumber == 1L) {
                continue
            }
            val name = record.get(RECORD_NAME)
            val stateName = record.get(RECORD_STATE)?.trim()?.ifEmpty { null }
            val state = stateName?.let { data -> states[data.uppercase()] }
            if (state == null && stateName != null) {
                errors.add(
                    ImportMessage(
                        location = record.recordNumber.toString(),
                        code = ErrorCode.SALES_TAX_STATE_NOT_FOUND,
                        message = "State not found: $stateName",
                    )
                )
            } else {
                var salesTax = salesTaxes.find { tax ->
                    tax.name.equals(name, true) && tax.stateId == state?.id
                }
                if (salesTax == null) {
                    salesTax = create(country, state, record)
                    salesTaxes.add(salesTax)
                    added++
                } else {
                    update(salesTax, record)
                    updated++
                }
                salesTaxIds.add(salesTax.id!!)
            }
        }

        /* deactivate */
        salesTaxes.filter { salesTax -> !salesTaxIds.contains(salesTax.id) }
            .forEach { salesTax -> deactivate(salesTax) }

        LOGGER.info("${added + updated} taxes(s) imported with $errors error(s)")
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

    private fun create(country: String, state: LocationEntity?, record: CSVRecord): SalesTaxEntity {
        return service.save(
            SalesTaxEntity(
                country = country,
                stateId = state?.id,
                active = true,
                name = record.get(RECORD_NAME),
                rate = record.get(RECORD_RATE).toDouble(),
                priority = record.get(RECORD_PRIORITY).toInt(),
            )
        )
    }

    private fun update(salesTax: SalesTaxEntity, record: CSVRecord) {
        salesTax.active = true
        salesTax.rate = record.get(RECORD_RATE).toDouble()
        salesTax.priority = record.get(RECORD_PRIORITY).toInt()
        service.save(salesTax)
    }

    private fun deactivate(salesTax: SalesTaxEntity) {
        salesTax.active = false
        service.save(salesTax)
    }
}
