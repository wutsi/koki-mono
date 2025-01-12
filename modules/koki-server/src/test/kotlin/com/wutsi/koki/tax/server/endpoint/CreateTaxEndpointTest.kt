package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tax.dto.GetTaxTypeResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/GetTaxTypeEndpoint.sql"])
class GetTaxTypeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/tax-types/100", GetTaxTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val tax = result.body!!.taxType
        assertEquals("a", tax.name)
        assertEquals("title-a", tax.title)
        assertEquals("description-a", tax.description)
    }

    @Test
    fun `bad id`() {
        val result = rest.getForEntity("/v1/tax-types/99999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_TYPE_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/tax-types/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_TYPE_NOT_FOUND, result.body?.error?.code)
    }
}
