package com.wutsi.koki.tenant.server.io

import com.wutsi.koki.tenant.server.domain.AttributeEntity.Companion.CSV_HEADERS
import com.wutsi.koki.tenant.server.service.AttributeService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

@Service
class AttributeCSVExporter(
    private val service: AttributeService,
) {
    fun export(tenantId: Long, output: OutputStream) {
        val writer = BufferedWriter(OutputStreamWriter(output))
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader(*CSV_HEADERS.toTypedArray())
                    .build(),
            )
            printer.use {
                val attributes = service.search(tenantId)
                attributes.forEach {
                    printer.printRecord(
                        it.name,
                        it.type.name,
                        it.active,
                        it.choices?.replace('\n', '|'),
                        it.label,
                        it.description
                    )
                }
                printer.flush()
            }
        }
    }
}
