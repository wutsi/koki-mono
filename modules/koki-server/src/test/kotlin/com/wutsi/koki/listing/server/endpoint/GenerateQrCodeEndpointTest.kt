package com.wutsi.koki.listing.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.GenerateQrCodeResponse
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.tenant.server.service.QrCodeGenerator
import com.wutsi.koki.tenant.server.service.StorageProvider
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/GenerateQrCodeEndpoint.sql"])
class GenerateQrCodeEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @MockitoBean
    private lateinit var storageProvider: StorageProvider

    @MockitoBean
    private lateinit var qrCodeGenerator: QrCodeGenerator

    private val storage = mock<StorageService>()

    @Test
    fun generate() {
        // GIVEN
        val url = URL("https://picsum.photos/750/750")
        doReturn(url).whenever(storage).store(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )
        doReturn(storage).whenever(storageProvider).get(any())

        // WHEN
        val response = rest.postForEntity("/v1/listings/100/qr-code", null, GenerateQrCodeResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(100L, response.body?.listingId)
        assertEquals(url.toString(), response.body?.qrCodeUrl)

        verify(qrCodeGenerator).generate(
            eq("https://client.tenant-1.com/qr-codes/listings/100"),
            any(),
            any()
        )

        verify(storage).store(
            eq("tenant/1/listing/100/qr-code/default.png"),
            any(),
            eq("image/png"),
            any(),
        )

        val listing = dao.findById(100L).get()
        assertEquals(response.body?.qrCodeUrl, listing.qrCodeUrl)
    }
}
