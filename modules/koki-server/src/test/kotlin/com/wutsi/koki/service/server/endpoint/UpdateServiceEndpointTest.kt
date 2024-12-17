package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.service.dto.AuthenticationType
import com.wutsi.koki.service.dto.CreateServiceRequest
import com.wutsi.koki.service.dto.CreateServiceResponse
import com.wutsi.koki.service.server.dao.ServiceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/service/CreateServiceEndpoint.sql"])
class CreateServiceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ServiceRepository

    val request = CreateServiceRequest(
        name = "SRV-555",
        title = "Test Service",
        description = "This is the description of a service",
        authenticationType = AuthenticationType.BASIC_AUTHENTICATION,
        username = "srv-user",
        password = "secret",
        apiKey = "flkfd-fdoifdkfd0039209",
        active = true,
        baseUrl = "https://localhost:80802"
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/services", request, CreateServiceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val serviceId = response.body!!.serviceId
        val service = dao.findById(serviceId).get()

        assertEquals(TENANT_ID, service.tenantId)
        assertEquals(request.name, service.name)
        assertEquals(request.title, service.title)
        assertEquals(request.description, service.description)
        assertEquals(request.authenticationType, service.authenticationType)
        assertEquals(request.username, service.username)
        assertEquals(request.password, service.password)
        assertEquals(request.apiKey, service.apiKey)
        assertEquals(request.active, service.active)
        assertEquals(request.baseUrl, service.baseUrl)
    }

    @Test
    fun duplicate() {
        val result = rest.postForEntity("/v1/services", request.copy(name = "SRV-100"), ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.SERVICE_DUPLICATE_NAME, result.body!!.error.code)
    }
}
