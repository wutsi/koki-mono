package com.wutsi.koki.tracking.server.dao

import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.DeviceType
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.tracking.server.domain.TrackEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
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
class TrackRepository(private val storageServiceBuilder: StorageServiceBuilder) {
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
            "rank",
            "component",
        )
    }

    fun read(input: InputStream): List<TrackEntity> {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*HEADERS)
                .setAllowMissingColumnNames(true)
                .get(),
        )
        return parser.map {
            TrackEntity(
                time = toLong(it.get("time")),
                correlationId = it.get("correlation_id"),
                tenantId = toLong(it.get("tenant_id")),
                deviceId = it.get("device_id"),
                accountId = it.get("account_id"),
                productId = it.get("product_id"),
                page = it.get("page"),
                event = it.get("event")?.ifEmpty { null }?.let { event ->
                    try {
                        TrackEvent.valueOf(event.uppercase())
                    } catch (ex: Exception) {
                        null
                    }
                } ?: TrackEvent.UNKNOWN,
                channelType = it.get("channel_type")?.ifEmpty { null }?.let { event ->
                    try {
                        ChannelType.valueOf(event.uppercase())
                    } catch (ex: Exception) {
                        null
                    }
                } ?: ChannelType.UNKNOWN,
                value = it.get("value"),
                ip = it.get("ip"),
                lat = toDouble(it.get("lat")),
                long = toDouble(it.get("long")),
                bot = it.get("bot").toBoolean(),
                deviceType = it.get("device_type")?.ifEmpty { null }?.let { type ->
                    try {
                        DeviceType.valueOf(type.uppercase())
                    } catch (ex: Exception) {
                        null
                    }
                } ?: DeviceType.UNKNOWN,
                source = it.get("source"),
                campaign = it.get("campaign"),
                url = it.get("url"),
                referrer = it.get("referrer"),
                ua = it.get("ua"),
                country = it.get("country"),
                rank = toInt(it.get("rank")),
                component = it.get("component"),
            )
        }
    }

    fun save(date: LocalDate, items: List<TrackEntity>): URL {
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
                return storeToCloud(input, date, filename, file.length())
            }
        } finally {
            file.delete()
        }
    }

    fun dailyFolder(date: LocalDate): String {
        return "track/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    }

    fun monthlyFolder(date: LocalDate): String {
        return "track/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM"))
    }

    private fun storeLocally(items: List<TrackEntity>, out: OutputStream) {
        val writer = BufferedWriter(OutputStreamWriter(out))
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT.builder().setHeader(*HEADERS).get(),
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
                        it.rank,
                        it.component
                    )
                }
                printer.flush()
            }
        }
    }

    private fun storeToCloud(input: InputStream, date: LocalDate, filename: String, filesize: Long): URL {
        val folder = dailyFolder(date)
        return storageServiceBuilder.default().store("$folder/$filename", input, "text/csv", filesize)
    }

    protected fun toLong(str: String): Long =
        try {
            str.toLong()
        } catch (ex: Exception) {
            0L
        }

    protected fun toInt(str: String): Int =
        try {
            str.toInt()
        } catch (ex: Exception) {
            0
        }

    protected fun toDouble(str: String): Double =
        try {
            str.toDouble()
        } catch (ex: Exception) {
            0.0
        }
}
