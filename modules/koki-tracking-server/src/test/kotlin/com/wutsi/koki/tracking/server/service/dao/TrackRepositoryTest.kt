package com.wutsi.koki.tracking.server.service.dao

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
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
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrackRepositoryTest {
    @Autowired
    private lateinit var dao: TrackRepository

    @MockitoBean
    private lateinit var storageServiceBuilder: StorageServiceBuilder

    @Value("\${wutsi.platform.storage.local.directory}")
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
        val date = LocalDate.now(ZoneId.of("UTC"))
        val url = dao.save(date, arrayListOf(createTrack()))

        // THEN
        val out = ByteArrayOutputStream()
        storage.get(url, out)
        assertEquals(
            """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type,recipient_id
                3333,123,1,sample-device,333,1234,SR,VIEW,yo,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,LISTING,777
            """.trimIndent(),
            out.toString().trimIndent(),
        )
    }

    @Test
    fun read() {
        // GIVEN
        val csv = """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type,recipient_id
                3333,123,1,sample-device,333,1234,SR,VIEW,yo,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,LISTING,7777
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(1, tracks.size)
        assertEquals(3333L, tracks[0].time)
        assertEquals("123", tracks[0].correlationId)
        assertEquals("sample-device", tracks[0].deviceId)
        assertEquals("333", tracks[0].accountId)
        assertEquals("1234", tracks[0].productId)
        assertEquals("SR", tracks[0].page)
        assertEquals(TrackEvent.VIEW, tracks[0].event)
        assertEquals("yo", tracks[0].value)
        assertEquals("1.1.2.3", tracks[0].ip)
        assertEquals(111.0, tracks[0].long)
        assertEquals(222.0, tracks[0].lat)
        assertEquals(false, tracks[0].bot)
        assertEquals(DeviceType.DESKTOP, tracks[0].deviceType)
        assertEquals(ChannelType.WEB, tracks[0].channelType)
        assertEquals("facebook", tracks[0].source)
        assertEquals("12434554", tracks[0].campaign)
        assertEquals(
            "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
            tracks[0].url,
        )
        assertEquals("https://www.google.ca", tracks[0].referrer)
        assertEquals(
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
            tracks[0].ua,
        )
        assertEquals("CM", tracks[0].country)
        assertEquals(11, tracks[0].rank)
        assertEquals("map", tracks[0].component)
        assertEquals(ObjectType.LISTING, tracks[0].productType)
        assertEquals(7777L, tracks[0].recipientId)
    }

    @Test
    fun `read - empty event`() {
        // GIVEN
        val csv = """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type,recipient_id
                3333,123,1,sample-device,333,1234,SR,,yo,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,LISTING,
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(TrackEvent.UNKNOWN, tracks[0].event)
    }

    @Test
    fun `read - invalid event`() {
        // GIVEN
        val csv = """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type,recipient_id
                3333,123,1,sample-device,333,1234,SR,xxxxx,yo,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,LISTING,
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(TrackEvent.UNKNOWN, tracks[0].event)
    }

    @Test
    fun `read - invalid channel_type`() {
        // GIVEN
        val csv = """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type,recipient_id
                3333,123,1,sample-device,333,1234,SR,VIEW,yo,1.1.2.3,111.0,222.0,false,DESKTOP,xxx,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,LISTING,
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(ChannelType.UNKNOWN, tracks[0].channelType)
    }

    @Test
    fun `read - empty channel_type`() {
        // GIVEN
        val csv = """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type,recipient_id
                3333,123,1,sample-device,333,1234,SR,VIEW,yo,1.1.2.3,111.0,222.0,false,DESKTOP,,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,LISTING,
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(ChannelType.UNKNOWN, tracks[0].channelType)
    }

    @Test
    fun `read - invalid device_type`() {
        // GIVEN
        val csv = """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type,recipient_id
                3333,123,1,sample-device,333,1234,SR,VIEW,yo,1.1.2.3,111.0,222.0,false,xxx,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,LISTING,
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(DeviceType.UNKNOWN, tracks[0].deviceType)
    }

    @Test
    fun `read - empty device_type`() {
        // GIVEN
        val csv = """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type
                3333,123,1,sample-device,333,1234,SR,VIEW,yo,1.1.2.3,111.0,222.0,false,,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,LISTING
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(DeviceType.UNKNOWN, tracks[0].deviceType)
    }

    @Test
    fun `read - invalid product_type`() {
        // GIVEN
        val csv = """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type,recipient_id
                3333,123,1,sample-device,333,1234,SR,VIEW,yo,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,xxx,
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(ObjectType.UNKNOWN, tracks[0].productType)
    }

    @Test
    fun `read - empty product_type`() {
        // GIVEN
        val csv = """
                time,correlation_id,tenant_id,device_id,account_id,product_id,page,event,value,ip,long,lat,bot,device_type,channel_type,source,campaign,url,referrer,ua,country,rank,component,product_type,recipient_id
                3333,123,1,sample-device,333,1234,SR,VIEW,yo,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),CM,11,map,,
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(ObjectType.UNKNOWN, tracks[0].productType)
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
        component = "map",
        productType = ObjectType.LISTING,
        recipientId = 777,
    )
}
