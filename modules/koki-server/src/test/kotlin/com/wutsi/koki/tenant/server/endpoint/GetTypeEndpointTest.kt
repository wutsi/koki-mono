package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.GetTypeResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GetTypeEndpoint.sql"])
class GetTypeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/types/100", GetTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val contact = result.body!!.type
        assertEquals("a", contact.name)
        assertEquals(ObjectType.ACCOUNT, contact.objectType)
        assertEquals("title-a", contact.title)
        assertEquals("description-a", contact.description)
    }

    @Test
    fun `bad id`() {
        val result = rest.getForEntity("/v1/types/99999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TYPE_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/types/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TYPE_NOT_FOUND, result.body?.error?.code)
    }
}
