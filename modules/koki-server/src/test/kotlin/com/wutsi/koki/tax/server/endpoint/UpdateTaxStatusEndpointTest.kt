package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tax.dto.UpdateTaxRequest
import com.wutsi.koki.tax.server.dao.TaxRepository
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/UpdateTaxEndpoint.sql"])
class UpdateTaxEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxRepository

    private val request = UpdateTaxRequest(
        taxTypeId = 110,
        fiscalYear = 2024,
        description = "New Taxes for 2025",
        startAt = DateUtils.addDays(Date(), 7),
        dueAt = DateUtils.addDays(Date(), 37),
        accountantId = 555L,
    )

    @Test
    fun update() {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val result = rest.postForEntity("/v1/taxes/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxId = 100L
        val tax = dao.findById(taxId).get()
        assertEquals(request.taxTypeId, tax.taxTypeId)
        assertEquals(request.fiscalYear, tax.fiscalYear)
        assertEquals(request.description, tax.description)
        assertEquals(fmt.format(request.dueAt), fmt.format(tax.dueAt))
        assertEquals(fmt.format(request.startAt), fmt.format(tax.startAt))
        assertEquals(request.accountantId, tax.accountantId)
        assertEquals(USER_ID, tax.modifiedById)
    }

    @Test
    fun notFound() {
        val result = rest.postForEntity("/v1/taxes/9999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun deleted() {
        val result = rest.postForEntity("/v1/taxes/199", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun anotherTenant() {
        val result = rest.postForEntity("/v1/taxes/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_NOT_FOUND, result.body!!.error.code)
    }
}
