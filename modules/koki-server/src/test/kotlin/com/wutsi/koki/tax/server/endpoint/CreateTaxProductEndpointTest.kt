package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tax.dto.CreateTaxProductRequest
import com.wutsi.koki.tax.dto.CreateTaxProductResponse
import com.wutsi.koki.tax.server.dao.TaxProductRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/CreateTaxProductEndpoint.sql"])
class CreateTaxProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxProductRepository

    @Test
    fun create() {
        val request = CreateTaxProductRequest(
            taxId = 100L,
            productId = 111L,
            unitPrice = 100.0,
            description = "Yo man",
            quantity = 5
        )
        val result = rest.postForEntity("/v1/tax-products", request, CreateTaxProductResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.taxProductId
        val taxProduct1 = dao.findById(id).get()
        assertEquals(TENANT_ID, taxProduct1.tenantId)
        assertEquals(request.taxId, taxProduct1.taxId)
        assertEquals(request.productId, taxProduct1.productId)
        assertEquals(request.unitPrice, taxProduct1.unitPrice)
        assertEquals(request.quantity, taxProduct1.quantity)
        assertEquals(request.quantity * request.unitPrice, taxProduct1.subTotal)
        assertEquals(request.description, taxProduct1.description)
    }
}
