package com.wutsi.koki.tenant.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.SendPasswordResponse
import com.wutsi.koki.tenant.dto.SendUsernameRequest
import com.wutsi.koki.tenant.server.command.SendPasswordCommand
import com.wutsi.koki.tenant.server.dao.PasswordResetTokenRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SendPasswordEndpoint.sql"])
class SendPasswordEndpointTest : TenantAwareEndpointTest() {
    @MockitoBean
    private lateinit var publisher: Publisher

    @Autowired
    private lateinit var dao: PasswordResetTokenRepository

    @Test
    fun send() {
        val request = SendUsernameRequest(
            email = "ray.sponsible@gmail.com"
        )
        val result = rest.postForEntity("/v1/users/password/send", request, SendPasswordResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val tokenId = result.body!!.tokenId
        val command = argumentCaptor<SendPasswordCommand>()
        verify(publisher).publish(command.capture())

        assertEquals(tokenId, command.firstValue.tokenId)
        assertEquals(1L, command.firstValue.tenantId)

        val token = dao.findById(tokenId).get()
        assertEquals(TENANT_ID, token.tenantId)
        assertEquals(11L, token.user.id)
        assertEquals(true, token.expiresAt.after(token.createdAt))
    }

    @Test
    fun `email not found`() {
        val request = SendUsernameRequest(
            email = "xxx@gmail.com"
        )
        val result = rest.postForEntity("/v1/users/password/send", request, ErrorResponse::class.java)

        verify(publisher, never()).publish(any())
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `user of another tenant`() {
        val request = SendUsernameRequest(
            email = "roger.milla@gmail.com"
        )
        val result = rest.postForEntity("/v1/users/password/send", request, ErrorResponse::class.java)

        verify(publisher, never()).publish(any())
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }
}
