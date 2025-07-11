package com.wutsi.koki.tracking.server.service.dao

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.tracking.server.dao.KpiRoomRepository
import com.wutsi.koki.tracking.server.domain.KpiRoomEntity
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KpiRoomRepositoryTest {
    @Autowired
    private lateinit var dao: KpiRoomRepository

    @MockitoBean
    private lateinit var storageServiceBuilder: StorageServiceBuilder

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var directory: String

    private lateinit var storage: StorageService

    @BeforeEach
    fun setUp() {
        File("$directory/kpi").deleteRecursively()

        storage = LocalStorageService(directory, "http://localhost:8083/local-storage")
        doReturn(storage).whenever(storageServiceBuilder).default()
    }

    @Test
    fun save() {
        // WHEN
        val url = dao.save(LocalDate.now(), arrayListOf(createKpi()))

        // THEN
        val out = ByteArrayOutputStream()
        storage.get(url, out)
        assertEquals(
            """
                tenant_id,product_id,total_impressions,total_clicks,total_views,total_messages,total_visitors
                1,1234,100,11,10,2,10
            """.trimIndent(),
            out.toString().trimIndent(),
        )
    }

    private fun createKpi() = KpiRoomEntity(
        productId = "1234",
        tenantId = 1L,
        totalVisitors = 10,
        totalClicks = 11,
        totalMessages = 2,
        totalViews = 13,
        totalImpressions = 100,
    )
}
