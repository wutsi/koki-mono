package com.wutsi.koki.room.server.service


import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.sql.Statement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.sql.DataSource

@Service
class KpiRoomImporter(
    private val ds: DataSource,
    private val storageServiceBuilder: StorageServiceBuilder,
    private val logger: KVLogger,
) {
    fun import(date: LocalDate) {
        try {
            val file = download(date)

            val cnn = ds.connection
            cnn.use {
                val stmt = cnn.createStatement()
                stmt.use {
                    import(date, file, stmt)
                }
            }
        } catch (ex: Exception) {
            logger.setException(ex)
        }
    }

    private fun import(date: LocalDate, file: File, stmt: Statement) {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder
                .create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(
                    "tenant_id",
                    "product_id",
                    "total_impressions",
                    "total_clicks",
                    "total_views",
                    "total_messages",
                    "total_visitors",
                ).build(),
        )

        // Load kpis
        parser.use {
            for (record in parser) {
                import(date, record, stmt)
            }
        }
    }

    private fun import(date: LocalDate, record: CSVRecord, stmt: Statement) {
        val tenantId = record.get("tenant_id").toLong()
        val productId = record.get("product_id").toLong()
        val totalImpressions = record.get("total_impressions").toLong()
        val totalClicks = record.get("total_clicks").toLong()
        val totalViews = record.get("total_views").toLong()
        val totalMessages = record.get("total_messages").toLong()
        val totalVisitors = record.get("total_visitors").toLong()
        val ctr = if (totalImpressions == 0L) 0.0 else totalClicks.toDouble() / totalImpressions.toDouble()
        val cvr = if (totalImpressions == 0L) 0.0 else totalMessages.toDouble() / totalImpressions.toDouble()

        val sql = """
                INSERT INTO T_KPI_ROOM(
                    tenant_fk,
                    room_fk,
                    period,
                    total_impressions,
                    total_clicks,
                    total_views,
                    total_messages,
                    total_visitors,
                    ctr,
                    cvr
                ) VALUES (
                    $tenantId,
                    $productId,
                    '$date',
                    $totalImpressions,
                    $totalClicks,
                    $totalViews,
                    $totalMessages,
                    $totalVisitors,
                    $ctr,
                    $cvr
                )
                ON DUPLICATE KEY UPDATE
                    total_impressions=$totalImpressions,
                    total_clics=$totalClicks,
                    total_views=$totalViews,
                    total_messages=$totalMessages,
                    total_visitors=$totalVisitors,
                    ctr=$ctr,
                    cvr=$cvr
        """.trimIndent()
        stmt.execute(sql)
    }

    private fun download(date: LocalDate): File {
        val file = File.createTempFile(UUID.randomUUID().toString(), ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            val storage = storageServiceBuilder.default()
            val path = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/rooms.csv"
            val url = storage.toURL(path)

            logger.add("url", url)
            storage.get(url, fout)
        }
        return file
    }
}
