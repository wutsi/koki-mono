package com.wutsi.koki.tenant.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.SendUsernameRequest
import com.wutsi.koki.tenant.server.command.SendUsernameCommand
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SendUsernameEndpoint.sql"])
class SendUsernameEndpointTest : TenantAwareEndpointTest() {
    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun get() {
        val request = SendUsernameRequest(
            email = "ray.sponsible@gmail.com"
        )
        val result = rest.postForEntity("/v1/users/username/send", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val command = argumentCaptor<SendUsernameCommand>()
        verify(publisher).publish(command.capture())

        assertEquals(11L, command.firstValue.userId)
        assertEquals(1L, command.firstValue.tenantId)
    }

    @Test
    fun notFound() {
        val request = SendUsernameRequest(
            email = "xxx@gmail.com"
        )
        val result = rest.postForEntity("/v1/users/username/send", request, ErrorResponse::class.java)

        verify(publisher, never()).publish(any())
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `user of another tenant`() {
        val request = SendUsernameRequest(
            email = "roger.milla@gmail.com"
        )
        val result = rest.postForEntity("/v1/users/username/send", request, ErrorResponse::class.java)

        verify(publisher, never()).publish(any())
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }
}
