package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tax.dto.CreateTaxRequest
import com.wutsi.koki.tax.dto.CreateTaxResponse
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.server.dao.TaxRepository
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/CreateTaxEndpoint.sql"])
class CreateTaxEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxRepository

    @Test
    fun create() {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val request = CreateTaxRequest(
            accountId = 111,
            taxTypeId = 100,
            fiscalYear = 2024,
            description = "New Taxes for 2025",
            startAt = DateUtils.addDays(Date(), 7),
            dueAt = DateUtils.addDays(Date(), 37),
            accountantId = 555L,
        )
        val result = rest.postForEntity("/v1/taxes", request, CreateTaxResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxId = result.body!!.taxId
        val tax = dao.findById(taxId).get()
        assertEquals(request.accountId, tax.accountId)
        assertEquals(request.taxTypeId, tax.taxTypeId)
        assertEquals(request.fiscalYear, tax.fiscalYear)
        assertEquals(request.description, tax.description)
        assertEquals(fmt.format(request.dueAt), fmt.format(tax.dueAt))
        assertEquals(fmt.format(request.startAt), fmt.format(tax.startAt))
        assertEquals(request.accountantId, tax.accountantId)
        assertEquals(USER_ID, tax.createdById)
        assertEquals(USER_ID, tax.modifiedById)
        assertEquals(TaxStatus.NEW, tax.status)
    }

    @Test
    fun `inherit accountant`() {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val request = CreateTaxRequest(
            accountId = 111,
            taxTypeId = 100,
            fiscalYear = 2024,
            description = "New Taxes for 2025",
            startAt = DateUtils.addDays(Date(), 7),
            dueAt = DateUtils.addDays(Date(), 37),
            accountantId = null,
        )
        val result = rest.postForEntity("/v1/taxes", request, CreateTaxResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxId = result.body!!.taxId
        val tax = dao.findById(taxId).get()
        assertEquals(request.accountId, tax.accountId)
        assertEquals(request.taxTypeId, tax.taxTypeId)
        assertEquals(request.fiscalYear, tax.fiscalYear)
        assertEquals(request.description, tax.description)
        assertEquals(fmt.format(request.dueAt), fmt.format(tax.dueAt))
        assertEquals(fmt.format(request.startAt), fmt.format(tax.startAt))
        assertEquals(11L, tax.accountantId)
        assertEquals(USER_ID, tax.createdById)
        assertEquals(USER_ID, tax.modifiedById)
        assertEquals(TaxStatus.NEW, tax.status)
    }
}
