package com.wutsi.koki.tracking.server.dao

import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.tracking.server.domain.TrackEntity
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class TrackRepository(private val storage: StorageService) {
    companion object {
        private val HEADERS = arrayOf(
            "time",
            "correlation_id",
            "tenant_id",
            "device_id",
            "account_id",
            "product_id",
            "page",
            "event",
            "value",
            "ip",
            "long",
            "lat",
            "bot",
            "device_type",
            "channel_type",
            "source",
            "campaign",
            "url",
            "referrer",
            "ua",
            "country",
        )
    }

    fun save(items: List<TrackEntity>): URL {
        val filename = UUID.randomUUID().toString() + ".csv"
        val file = File.createTempFile(filename, "csv")
        try {
            // Store to file
            val output = FileOutputStream(file)
            output.use {
                storeLocally(items, output)
            }

            // Store to cloud
            val input = FileInputStream(file)
            input.use {
                val date = LocalDate.now(ZoneId.of("UTC"))
                return storeToCloud(input, date, filename)
            }
        } finally {
            file.delete()
        }
    }

    private fun storeLocally(items: List<TrackEntity>, out: OutputStream) {
        val writer = BufferedWriter(OutputStreamWriter(out))
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader(*HEADERS)
                    .build(),
            )
            printer.use {
                items.forEach {
                    printer.printRecord(
                        it.time,
                        it.correlationId,
                        it.tenantId,
                        it.deviceId,
                        it.accountId,
                        it.productId,
                        it.page,
                        it.event,
                        it.value,
                        it.ip,
                        it.long,
                        it.lat,
                        it.bot,
                        it.deviceType,
                        it.channelType,
                        it.source,
                        it.campaign,
                        it.url,
                        it.referrer,
                        it.ua,
                        it.country,
                    )
                }
                printer.flush()
            }
        }
    }

    private fun storeToCloud(input: InputStream, date: LocalDate, filename: String): URL {
        val folder = "track/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        return storage.store("$folder/$filename", input, "text/csv", Long.MAX_VALUE)
    }
}
