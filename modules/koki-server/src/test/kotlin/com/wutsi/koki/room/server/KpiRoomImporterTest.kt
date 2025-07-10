package com.wutsi.koki.room.server

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.room.server.dao.KpiRoomRepository
import com.wutsi.koki.room.server.service.KpiRoomImporter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
import java.time.LocalDate
import kotlin.test.Test

@Sql(value = ["/db/test/clean.sql", "/db/test/room/KpiRoomImporter.sql"])
class KpiRoomImporterTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var storageServiceBuilder: StorageServiceBuilder

    @Autowired
    private lateinit var importer: KpiRoomImporter

    @Autowired
    private lateinit var dao: KpiRoomRepository

    @Test
    fun import() {
        val file = """
            tenant_id,product_id,total_impressions,total_clicks,total_views,total_messages,total_visitors
            1,111,200,40,50,35,150
            1,200,xxx,15,20,15,120
            1,112,150,15,20,15,120
        """.trimIndent()
        storageServiceBuilder.default().store(
            path = "kpi/2020/01/rooms.csv",
            content = ByteArrayInputStream(file.toByteArray()),
            contentType = "text/csv",
            contentLength = file.length.toLong()
        )

        val response = importer.import(LocalDate.of(2020, 1, 10))

        assertEquals(0, response.added)
        assertEquals(1, response.errors)
        assertEquals(2, response.updated)

        val period = LocalDate.of(2020, 1, 1)
        val room112 = dao.findByRoomIdAndPeriod(112L, period)
        assertNotNull(room112)
        assertEquals(1L, room112.tenantId)
        assertEquals(112L, room112.roomId)
        assertEquals(150L, room112.totalImpressions)
        assertEquals(15L, room112.totalClicks)
        assertEquals(20L, room112.totalViews)
        assertEquals(15L, room112.totalMessages)
        assertEquals(120L, room112.totalVisitors)
        assertEquals(0.10, room112.ctr)
        assertEquals(0.10, room112.cvr)

        val room111 = dao.findByRoomIdAndPeriod(111L, period)
        assertNotNull(room111)
        assertEquals(1L, room111.tenantId)
        assertEquals(111L, room111.roomId)
        assertEquals(200L, room111.totalImpressions)
        assertEquals(40L, room111.totalClicks)
        assertEquals(50L, room111.totalViews)
        assertEquals(35L, room111.totalMessages)
        assertEquals(150L, room111.totalVisitors)
        assertEquals(0.20, room111.ctr)
        assertEquals(0.175, room111.cvr)
    }
}
