package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.dto.GetServiceResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/service/GetServiceEndpoint.sql"])
class GetServiceEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/services/100", GetServiceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val service = response.body!!.service

        assertEquals("SRV-100", service.name)
        assertEquals("Service #100", service.title)
        assertEquals("Description of service", service.description)
        assertEquals(AuthorizationType.BASIC, service.authorizationType)
        assertEquals("admin", service.username)
        assertEquals("secret", service.password)
        assertEquals("api-key-00000", service.apiKey)
        assertEquals(true, service.active)
        assertEquals("https://localhost:7555", service.baseUrl)
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/services/99999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SERVICE_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun deleted() {
        val result = rest.getForEntity("/v1/services/199", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SERVICE_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/services/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SERVICE_NOT_FOUND, result.body!!.error.code)
    }
}
