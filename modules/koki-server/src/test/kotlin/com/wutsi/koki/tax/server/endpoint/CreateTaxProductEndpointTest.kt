package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tax.dto.CreateTaxProductRequest
import com.wutsi.koki.tax.dto.CreateTaxProductResponse
import com.wutsi.koki.tax.dto.Offer
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
            offers = listOf(
                Offer(productId = 111L, unitPrice = 100.0),
                Offer(productId = 222L, unitPrice = 50.0),
            )
        )
        val result = rest.postForEntity("/v1/tax-products", request, CreateTaxProductResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val ids = result.body!!.taxProductIds
        assertEquals(2, ids.size)

        val taxProduct1 = dao.findByTaxIdAndProductId(request.taxId, request.offers[0].productId)!!
        assertEquals(TENANT_ID, taxProduct1.tenantId)
        assertEquals(request.taxId, taxProduct1.taxId)
        assertEquals(100.0, taxProduct1.unitPrice)
        assertEquals(4, taxProduct1.quantity)
        assertEquals(400.0, taxProduct1.subTotal)

        val taxProduct2 = dao.findByTaxIdAndProductId(request.taxId, request.offers[1].productId)!!
        assertEquals(TENANT_ID, taxProduct2.tenantId)
        assertEquals(request.taxId, taxProduct2.taxId)
        assertEquals(50.0, taxProduct2.unitPrice)
        assertEquals(1, taxProduct2.quantity)
        assertEquals(50.0, taxProduct2.subTotal)
    }
}
