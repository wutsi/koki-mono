package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tax.server.dao.TaxRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/DeleteTaxEndpoint.sql"])
class DeleteTaxEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxRepository

    @Test
    fun delete() {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm")
        rest.delete("/v1/taxes/100")

        val taxId = 100L
        val tax = dao.findById(taxId).get()
        assertEquals(USER_ID, tax.deletedById)
        assertEquals(true, tax.deleted)
        assertEquals(fmt.format(Date()), fmt.format(tax.deletedAt))
    }
}
