package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tax.dto.GetTaxFileResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/GetTaxFileEndpoint.sql"])
class GetTaxFileEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/tax-files/111", GetTaxFileResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxFile = result.body!!.taxFile
        assertEquals(111L, taxFile.fileId)
        assertEquals(100L, taxFile.taxId)
        assertEquals("en", taxFile.data.language)
        assertEquals(10, taxFile.data.numberOfPages)
        assertEquals("Yo", taxFile.data.description)
        assertEquals(1, taxFile.data.contacts.size)
        assertEquals("Ray", taxFile.data.contacts[0].firstName)
        assertEquals("Sponsible", taxFile.data.contacts[0].lastName)
    }

    @Test
    fun `not found`() {
        val result = rest.getForEntity("/v1/tax-files/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_FILE_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/tax-files/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_FILE_NOT_FOUND, result.body?.error?.code)
    }
}
