package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tax.dto.GetTaxProductResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/GetTaxProductEndpoint.sql"])
class GetTaxProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/tax-products/100", GetTaxProductResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxProduct = result.body!!.taxProduct
        assertEquals(100L, taxProduct.taxId)
        assertEquals(111L, taxProduct.productId)
        assertEquals(11100L, taxProduct.unitPriceId)
        assertEquals(150.0, taxProduct.unitPrice)
        assertEquals(3, taxProduct.quantity)
        assertEquals(450.0, taxProduct.subTotal)
        assertEquals("CAD", taxProduct.currency)
        assertEquals("Yo man...", taxProduct.description)
    }

    @Test
    fun `not found`() {
        val result = rest.getForEntity("/v1/tax-products/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_PRODUCT_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/tax-products/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.TAX_PRODUCT_NOT_FOUND, result.body?.error?.code)
    }
}
