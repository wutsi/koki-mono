package com.wutsi.koki.refdata.server.io

import com.wutsi.koki.common.dto.ImportMessage
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.refdata.server.domain.JuridictionEntity
import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import com.wutsi.koki.refdata.server.service.JuridictionService
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
    private val juridictionService: JuridictionService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SalesTaxImporter::class.java)

        private const val RECORD_NAME = 0
        private const val RECORD_JURIDICTION = 1
        private const val RECORD_RATE = 2
        private const val RECORD_PRIORITY = 3
    }

    fun import(country: String): ImportResponse {
        var added = 0
        var updated = 0
        var errors = mutableListOf<ImportMessage>()

        /* import */
        val filename = "/refdata/sales-tax/$country.csv"
        val input = SalesTaxImporter::class.java.getResourceAsStream(filename)
            ?: throw ConflictException(
                error = Error(
                    code = ErrorCode.SALES_TAX_COUNTRY_NOT_SUPPORTED,
                    message = "Resource not found $filename",
                )
            )

        val salesTaxes = service.getByCountry(country).toMutableList()
        val salesTaxIds = mutableListOf<Long>()
        val parser = createParser(input)
        for (record in parser) {
            if (record.recordNumber == 1L) {
                continue
            }
            val name = record.get(RECORD_NAME)
            val juridictionId = record.get(RECORD_JURIDICTION).toLong()
            val juridiction = juridictionService.getByIdOrNull(juridictionId)
            if (juridiction == null) {
                errors.add(
                    ImportMessage(
                        location = record.recordNumber.toString(),
                        code = ErrorCode.JURIDICTION_NOT_FOUND,
                        message = "Juridiction not found: $juridictionId",
                    )
                )
            } else {
                var salesTax = salesTaxes.find { tax ->
                    tax.name.equals(name, true) && tax.juridiction.id == juridiction?.id
                }
                if (salesTax == null) {
                    salesTax = create(juridiction, record)
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
            .forEach { salesTax ->
                deactivate(salesTax)
                updated++
            }

        LOGGER.info("${added + updated} tax(es) for $country imported with ${errors.size} error(s)")
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

    private fun create(juridiction: JuridictionEntity, record: CSVRecord): SalesTaxEntity {
        return service.save(
            SalesTaxEntity(
                juridiction = juridiction,
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
