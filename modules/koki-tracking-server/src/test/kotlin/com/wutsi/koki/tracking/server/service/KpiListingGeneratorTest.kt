package com.wutsi.koki.tracking.server.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import com.wutsi.koki.tracking.server.service.filter.PersisterFilter
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KpiListingGeneratorTest {
    @MockitoBean
    private lateinit var storageServiceBuilder: StorageServiceBuilder

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var directory: String

    @Autowired
    private lateinit var persister: PersisterFilter

    @Autowired
    private lateinit var consumer: TrackingConsumer

    @Autowired
    private lateinit var generator: KpiListingGenerator

    @BeforeEach
    fun setUp() {
        File("$directory/kpi").deleteRecursively()
        File("$directory/track").deleteRecursively()

        val storage = LocalStorageService(directory, "http://localhost:8083/local-storage")
        doReturn(storage).whenever(storageServiceBuilder).default()
    }

    @Test
    fun generate() {
        // GIVEN
        consume(createTrack(productId = "1", event = TrackEvent.IMPRESSION, rank = 0, deviceId = "d1"))
        consume(createTrack(productId = "1", event = TrackEvent.IMPRESSION, rank = 0, deviceId = "d2"))
        consume(createTrack(productId = "2", event = TrackEvent.IMPRESSION, rank = 1, deviceId = "d1"))
        consume(createTrack(productId = "3", event = TrackEvent.IMPRESSION, rank = 2, deviceId = "d1"))
        persister.flush()
        consume(createTrack(productId = "1", event = TrackEvent.CLICK, deviceId = "d1"))
        consume(createTrack(productId = "2", event = TrackEvent.CLICK, deviceId = "d1"))
        persister.flush()
        consume(createTrack(productId = "1", event = TrackEvent.VIEW, deviceId = "d1"))
        consume(createTrack(productId = "2", event = TrackEvent.VIEW, deviceId = "d1"))
        consume(createTrack(productId = "2", event = TrackEvent.VIEW, ua = "Twitterbot/1.0")) // BOT
        persister.flush()
        consume(createTrack(productId = "2", event = TrackEvent.MESSAGE, deviceId = "d1"))
        persister.flush()

        // WHEN
        generator.generate(LocalDate.now())

        // THEN
        val date = LocalDate.now(ZoneId.of("UTC"))
        val path = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/listings.csv"
        val file = File("$directory/$path")
        assertEquals(
            """
                tenant_id,product_id,total_impressions,total_clicks,total_views,total_messages,total_visitors
                1,1,2,1,2,0,2
                1,2,1,1,1,1,1
                1,3,1,0,1,0,1
            """.trimIndent(),
            file.readText().trimIndent(),
        )
    }

    private fun consume(track: Track) {
        consumer.consume(TrackSubmittedEvent(track))
    }

    private fun createTrack(
        productId: String? = "1234",
        event: TrackEvent = TrackEvent.VIEW,
        ua: String = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
        rank: Int? = null,
        deviceId: String? = null
    ) = Track(
        time = System.currentTimeMillis(),
        ua = ua,
        correlationId = "123",
        event = event,
        productId = productId,
        page = "SR",
        value = "yo",
        tenantId = 1L,
        long = 111.0,
        lat = 222.0,
        ip = "1.1.2.3",
        deviceId = deviceId,
        accountId = "333",
        referrer = "https://www.google.ca",
        url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
        channelType = ChannelType.WEB,
        rank = rank,
    )
}
