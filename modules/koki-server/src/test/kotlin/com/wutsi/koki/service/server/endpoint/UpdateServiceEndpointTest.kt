package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.service.dto.AuthorizationType
import com.wutsi.koki.service.dto.CreateServiceRequest
import com.wutsi.koki.service.server.dao.ServiceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/service/UpdateServiceEndpoint.sql"])
class UpdateServiceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ServiceRepository

    val request = CreateServiceRequest(
        name = "SRV-555",
        title = "Test Service",
        description = "This is the description of a service",
        authorizationType = AuthorizationType.BASIC,
        username = "srv-user",
        password = "secret",
        apiKey = "flkfd-fdoifdkfd0039209",
        active = true,
        baseUrl = "https://localhost:80802"
    )

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/services/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val service = dao.findById("100").get()

        assertEquals(TENANT_ID, service.tenantId)
        assertEquals(request.name, service.name)
        assertEquals(request.title, service.title)
        assertEquals(request.description, service.description)
        assertEquals(request.authorizationType, service.authorizationType)
        assertEquals(request.username, service.username)
        assertEquals(request.password, service.password)
        assertEquals(request.apiKey, service.apiKey)
        assertEquals(request.active, service.active)
        assertEquals(request.baseUrl, service.baseUrl)
    }

    @Test
    fun duplicate() {
        val result = rest.postForEntity("/v1/services/110", request.copy(name = "SRV-120"), ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.SERVICE_DUPLICATE_NAME, result.body!!.error.code)
    }

    @Test
    fun deleted() {
        val result = rest.postForEntity("/v1/services/199", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SERVICE_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun notFound() {
        val result = rest.postForEntity("/v1/services/999999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SERVICE_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.postForEntity("/v1/services/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.SERVICE_NOT_FOUND, result.body!!.error.code)
    }
}
