package com.wutsi.koki.room.server.service

import jakarta.transaction.Transactional
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.sql.DataSource

@Service
class KpiRoomPersister(private val ds: DataSource) {
    @Transactional
    fun persist(date: LocalDate, record: CSVRecord) {
        val tenantId = record.get("tenant_id").toLong()
        val productId = record.get("product_id").toLong()
        val totalImpressions = record.get("total_impressions").toLong()
        val totalClicks = record.get("total_clicks").toLong()
        val totalViews = record.get("total_views").toLong()
        val totalMessages = record.get("total_messages").toLong()
        val totalVisitors = record.get("total_visitors").toLong()
        val ctr = if (totalImpressions == 0L) 0.0 else totalClicks.toDouble() / totalImpressions.toDouble()
        val cvr = if (totalImpressions == 0L) 0.0 else totalMessages.toDouble() / totalImpressions.toDouble()
        val period = LocalDate.of(date.year, date.monthValue, 1)
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
                    '$period',
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
                    total_clicks=$totalClicks,
                    total_views=$totalViews,
                    total_messages=$totalMessages,
                    total_visitors=$totalVisitors,
                    ctr=$ctr,
                    cvr=$cvr
        """.trimIndent()

        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                stmt.execute(sql)
            }
        }
    }
}
