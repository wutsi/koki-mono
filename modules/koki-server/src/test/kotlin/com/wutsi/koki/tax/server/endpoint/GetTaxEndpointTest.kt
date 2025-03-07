package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tax.dto.GetTaxResponse
import com.wutsi.koki.tax.dto.TaxStatus
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/GetTaxEndpoint.sql"])
class GetTaxEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val result = rest.getForEntity("/v1/taxes/100", GetTaxResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val tax = result.body!!.tax
        assertEquals(100L, tax.taxTypeId)
        assertEquals(2014, tax.fiscalYear)
        assertEquals("2014 Tax Statements", tax.description)
        assertEquals("2014-04-30", fmt.format(tax.dueAt))
        assertEquals("2014-03-01", fmt.format(tax.startAt))
        assertEquals(110, tax.accountantId)
        assertEquals(111, tax.technicianId)
        assertEquals(112, tax.assigneeId)
        assertEquals(TaxStatus.PREPARING, tax.status)
        assertEquals(500.0, tax.totalRevenue)
        assertEquals("CAD", tax.currency)
        assertEquals(2, tax.productCount)
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/taxes/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun deleted() {
        val result = rest.getForEntity("/v1/taxes/199", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun anotherTenant() {
        val result = rest.getForEntity("/v1/taxes/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_NOT_FOUND, result.body!!.error.code)
    }
}
