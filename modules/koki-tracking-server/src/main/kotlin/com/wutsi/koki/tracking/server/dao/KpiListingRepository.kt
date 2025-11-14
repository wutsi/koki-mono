package com.wutsi.koki.tracking.server.dao

import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.tracking.server.domain.KpiListingEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class KpiListingRepository(private val storageServiceBuilder: StorageServiceBuilder) {
    companion object {
        private val HEADERS = arrayOf(
            "tenant_id",
            "product_id",
            "total_impressions",
            "total_clicks",
            "total_views",
            "total_messages",
            "total_visitors",
        )
    }

    fun save(date: LocalDate, items: List<KpiListingEntity>): URL {
        val file = File.createTempFile(UUID.randomUUID().toString(), "csv")
        try {
            // Store to file
            val output = FileOutputStream(file)
            output.use {
                storeLocally(items, output)
            }

            // Store to cloud
            val input = FileInputStream(file)
            input.use {
                return storeToCloud(input, date, "listings.csv", file.length())
            }
        } finally {
            file.delete()
        }
    }

    private fun storeLocally(items: List<KpiListingEntity>, out: OutputStream) {
        val writer = BufferedWriter(OutputStreamWriter(out))
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT.builder().setHeader(*HEADERS).build(),
            )
            printer.use {
                items.forEach {
                    printer.printRecord(
                        it.tenantId,
                        it.productId,
                        it.totalImpressions,
                        it.totalClicks,
                        it.totalVisitors,
                        it.totalMessages,
                        it.totalVisitors,
                    )
                }
                printer.flush()
            }
        }
    }

    private fun storeToCloud(input: InputStream, date: LocalDate, filename: String, filesize: Long): URL {
        val folder = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM"))
        return storageServiceBuilder.default().store("$folder/$filename", input, "text/csv", filesize)
    }
}
