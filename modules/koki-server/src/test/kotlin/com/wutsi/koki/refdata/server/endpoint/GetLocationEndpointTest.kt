package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.refdata.dto.GetLocationResponse
import com.wutsi.koki.refdata.dto.LocationType
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/refdata/GetLocationEndpoint.sql"])
class GetLocationEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/locations/2370201", GetLocationResponse::class.java)

        val location = response.body!!.location
        assertEquals("Bafoussam", location.name)
        assertEquals("CM", location.country)
        assertEquals(23702L, location.parentId)
        assertEquals(LocationType.CITY, location.type)
    }

    @Test
    fun `not found`() {
        val response = rest.getForEntity("/v1/locations/409493895489", ErrorResponse::class.java)

        assertEquals(404, response.statusCode.value())
        assertEquals(ErrorCode.LOCATION_NOT_FOUND, response.body?.error?.code)
    }
}
