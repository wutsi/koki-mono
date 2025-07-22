package com.wutsi.koki.room.server.service

import com.wutsi.koki.room.server.domain.KpiRoomEntity
import jakarta.transaction.Transactional
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.sql.DataSource

@Service
class KpiRoomPersister(private val ds: DataSource) {
    @Transactional
    fun persist(date: LocalDate, record: CSVRecord): Long {
        val tenantId = record.get("tenant_id").toLong()
        val roomId = record.get("product_id").toLong()
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
                    $roomId,
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

        execute(sql)
        return roomId
    }

    @Transactional
    fun aggregate(roomId: Long) {
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
                )
                    SELECT
                        tenant_fk,
                        room_fk,
                        '${KpiRoomEntity.OVERALL_PERIOD}',
                        total_impressions,
                        total_clicks,
                        total_views,
                        total_messages,
                        total_visitors,
                        ctr,
                        cvr
                    FROM
                    (
                        SELECT
                            tenant_fk,
                            room_fk,
                            SUM(total_impressions) as total_impressions,
                            SUM(total_clicks) as total_clicks,
                            SUM(total_views) as total_views,
                            SUM(total_messages) as total_messages,
                            AVG(total_visitors) as total_visitors,
                            IF(SUM(total_impressions)=0, 0, SUM(total_clicks)/SUM(total_impressions)) as cvr,
                            IF(SUM(total_impressions)=0, 0, SUM(total_messages)/SUM(total_impressions)) as ctr
                        FROM T_KPI_ROOM
                        WHERE period > '${KpiRoomEntity.OVERALL_PERIOD}' AND room_fk=$roomId
                        GROUP BY tenant_fk, room_fk
                    ) AS TMP
                    ON DUPLICATE KEY UPDATE
                        total_impressions=TMP.total_impressions,
                        total_clicks=TMP.total_clicks,
                        total_views=TMP.total_views,
                        total_messages=TMP.total_messages,
                        total_visitors=TMP.total_visitors,
                        ctr=TMP.ctr,
                        cvr=TMP.cvr
        """.trimIndent()
        execute(sql)
    }

    private fun execute(sql: String) {
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                stmt.execute(sql)
            }
        }
    }
}
