package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tax.dto.CreateTaxProductRequest
import com.wutsi.koki.tax.dto.CreateTaxProductResponse
import com.wutsi.koki.tax.server.dao.TaxProductRepository
import com.wutsi.koki.tax.server.dao.TaxRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/CreateTaxProductEndpoint.sql"])
class CreateTaxProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TaxProductRepository

    @Autowired
    private lateinit var taxDao: TaxRepository

    @Test
    fun create() {
        val request = CreateTaxProductRequest(
            taxId = 100L,
            productId = 111L,
            unitPriceId = 11100L,
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
        assertEquals(request.unitPriceId, taxProduct1.unitPriceId)
        assertEquals(request.quantity, taxProduct1.quantity)
        assertEquals(request.description, taxProduct1.description)
        assertEquals(150.0, taxProduct1.unitPrice)
        assertEquals("CAD", taxProduct1.currency)
        assertEquals(request.quantity * 150.0, taxProduct1.subTotal)

        val tax = taxDao.findById(taxProduct1.taxId).get()
        assertEquals(taxProduct1.subTotal, tax.totalRevenue)
        assertEquals(taxProduct1.currency, tax.currency)
        assertEquals(1, tax.productCount)
    }

    @Test
    fun `create without description`() {
        val request = CreateTaxProductRequest(
            taxId = 100L,
            productId = 111L,
            unitPriceId = 11100L,
            quantity = 5,
            description = null
        )
        val result = rest.postForEntity("/v1/tax-products", request, CreateTaxProductResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.taxProductId
        val taxProduct1 = dao.findById(id).get()
        assertEquals(TENANT_ID, taxProduct1.tenantId)
        assertEquals(request.taxId, taxProduct1.taxId)
        assertEquals(request.productId, taxProduct1.productId)
        assertEquals(request.unitPriceId, taxProduct1.unitPriceId)
        assertEquals(request.quantity, taxProduct1.quantity)
        assertEquals("Product 111", taxProduct1.description)
        assertEquals(150.0, taxProduct1.unitPrice)
        assertEquals("CAD", taxProduct1.currency)
        assertEquals(request.quantity * 150.0, taxProduct1.subTotal)

        val tax = taxDao.findById(taxProduct1.taxId).get()
        assertEquals(taxProduct1.subTotal, tax.totalRevenue)
        assertEquals(taxProduct1.currency, tax.currency)
    }

    @Test
    fun `invalid price`() {
        val request = CreateTaxProductRequest(
            taxId = 100L,
            productId = 111L,
            unitPriceId = 22200L,
            description = "Yo man",
            quantity = 5
        )
        val result = rest.postForEntity("/v1/tax-products", request, CreateTaxProductResponse::class.java)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode)
    }
}
