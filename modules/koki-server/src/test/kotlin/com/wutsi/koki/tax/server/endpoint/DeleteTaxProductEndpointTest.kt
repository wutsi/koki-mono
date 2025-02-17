package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tax.server.dao.TaxProductRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/DeleteTaxProductEndpoint.sql"])
class DeleteTaxProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxProductRepository

    @Test
    fun delete() {
        rest.delete("/v1/tax-products/100")

        val taxProduct = dao.findById(100)
        assertEquals(false, taxProduct.isPresent)
    }
}
