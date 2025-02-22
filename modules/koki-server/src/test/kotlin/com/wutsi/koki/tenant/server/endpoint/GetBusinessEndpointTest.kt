package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.GetBusinessResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GetBusinessEndpoint.sql"])
class GetBusinessEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/businesses", GetBusinessResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val business = result.body!!.business
        assertEquals("My Business", business.companyName)
        assertEquals("+5147580100", business.phone)
        assertEquals("+5147580111", business.fax)
        assertEquals("info@my-biz.com", business.email)
        assertEquals("https://my-biz.com", business.website)
        assertEquals("340 Pascal", business.address?.street)
        assertEquals("H7K1C6", business.address?.postalCode)
        assertEquals("CA", business.address?.country)
        assertEquals(111L, business.address?.cityId)
        assertEquals(100L, business.address?.stateId)
        assertEquals(listOf(1010L, 1011L), business.juridictionIds)
    }

    @Test
    @Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GetBusinessEndpoint-none.sql"])
    fun none() {
        val result = rest.getForEntity("/v1/businesses", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.BUSINESS_NOT_FOUND, result.body?.error?.code)
    }
}
