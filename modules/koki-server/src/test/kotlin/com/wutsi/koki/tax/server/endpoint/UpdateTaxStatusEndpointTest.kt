package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.UpdateTaxStatusRequest
import com.wutsi.koki.tax.server.dao.TaxRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/UpdateTaxStatusEndpoint.sql"])
class UpdateTaxStatusEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxRepository

    private val request = UpdateTaxStatusRequest(
        status = TaxStatus.FINALIZING,
        assigneeId = 888L,
    )

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/taxes/100/status", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxId = 100L
        val tax = dao.findById(taxId).get()
        assertEquals(request.status, tax.status)
        assertEquals(request.assigneeId, tax.assigneeId)
        assertEquals(USER_ID, tax.modifiedById)
    }
}
