package com.wutsi.koki.tracking.server.service.dao

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.DeviceType
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.tracking.server.dao.TrackRepository
import com.wutsi.koki.tracking.server.domain.TrackEntity
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrackRepositoryTest {
    @Autowired
    private lateinit var dao: TrackRepository

    @MockitoBean
    private lateinit var storageServiceBuilder: StorageServiceBuilder

    @Value("\${koki.storage.local.directory}")
    private lateinit var directory: String

    private lateinit var storage: StorageService

    @BeforeEach
    fun setUp() {
        File("$directory/track").deleteRecursively()

        storage = LocalStorageService(directory, "http://localhost:8083/local-storage")
        doReturn(storage).whenever(storageServiceBuilder).default()
    }

    @Test
    fun save() {
        // WHEN
        val url = dao.save(arrayListOf(createTrack()))

        // THEN
        val out = ByteArrayOutputStream()
        storage.get(url, out)
        assertEquals(
            """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component
                3333,123,1,sample-device,333,1234,SR,VIEW,yo,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map
            """.trimIndent(),
            out.toString().trimIndent(),
        )
    }

    private fun createTrack() = TrackEntity(
        time = 3333,
        ua = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
        correlationId = "123",
        bot = false,
        event = TrackEvent.VIEW,
        productId = "1234",
        page = "SR",
        value = "yo",
        tenantId = 1L,
        long = 111.0,
        lat = 222.0,
        ip = "1.1.2.3",
        deviceId = "sample-device",
        accountId = "333",
        referrer = "https://www.google.ca",
        url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
        deviceType = DeviceType.DESKTOP,
        channelType = ChannelType.WEB,
        source = "facebook",
        campaign = "12434554",
        country = "CM",
        rank = 11,
        component = "map"
    )
}
