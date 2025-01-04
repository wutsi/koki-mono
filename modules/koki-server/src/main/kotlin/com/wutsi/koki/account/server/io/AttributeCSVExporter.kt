package com.wutsi.koki.account.server.io

import com.wutsi.koki.account.server.domain.AttributeEntity
import com.wutsi.koki.account.server.service.AttributeService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

@Service
class AttributeCSVExporter(private val service: AttributeService) {
    fun export(output: OutputStream, tenantId: Long) {
        val writer = BufferedWriter(OutputStreamWriter(output))
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader(*AttributeEntity.CSV_HEADERS.toTypedArray())
                    .build(),
            )
            printer.use {
                val attributes = service.search(tenantId = tenantId, limit = Integer.MAX_VALUE)
                attributes.forEach {
                    printer.printRecord(
                        it.name,
                        it.type.name,
                        if (it.required) "yes" else "",
                        if (it.active) "yes" else "",
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
