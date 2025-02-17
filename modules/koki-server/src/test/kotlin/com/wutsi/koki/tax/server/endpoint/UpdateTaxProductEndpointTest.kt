package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tax.dto.UpdateTaxProductRequest
import com.wutsi.koki.tax.server.dao.TaxProductRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/UpdateTaxProductEndpoint.sql"])
class UpdateTaxProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxProductRepository

    @Test
    fun update() {
        val request = UpdateTaxProductRequest(
            quantity = 10,
            unitPrice = 170.0,
            description = "yo man"
        )
        val result = rest.postForEntity("/v1/tax-products/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxProduct = dao.findById(100).get()
        assertEquals(TENANT_ID, taxProduct.tenantId)
        assertEquals(100, taxProduct.taxId)
        assertEquals(request.unitPrice, taxProduct.unitPrice)
        assertEquals(request.quantity, taxProduct.quantity)
        assertEquals(request.unitPrice * request.quantity, taxProduct.subTotal)
        assertEquals(request.description, taxProduct.description)
    }
}
