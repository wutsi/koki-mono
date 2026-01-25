package com.wutsi.koki.agent.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.agent.dto.GenerateQrCodeResponse
import com.wutsi.koki.agent.server.dao.AgentRepository
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

@Sql(value = ["/db/test/clean.sql", "/db/test/agent/GenerateQrCodeEndpoint.sql"])
class GenerateQrCodeEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AgentRepository

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
        val response = rest.postForEntity("/v1/agents/100/qr-code", null, GenerateQrCodeResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(100L, response.body?.agentId)
        assertEquals(url.toString(), response.body?.qrCodeUrl)

        verify(qrCodeGenerator).generate(
            eq("https://client.tenant-1.com/agents/100"),
            any(),
            any()
        )

        val agent = dao.findById(100L).get()
        assertEquals(response.body?.qrCodeUrl, agent.qrCodeUrl)
    }
}
