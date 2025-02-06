package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.product.dto.GetProductResponse
import com.wutsi.koki.product.dto.ProductType
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/product/GetProductEndpoint.sql"])
class GetProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/products/100", GetProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = response.body!!.product
        assertEquals("RAY-123", product.code)
        assertEquals("Rayband 123", product.name)
        assertEquals("Glasses with class", product.description)
        assertEquals(true, product.active)
        assertEquals(ProductType.PHYSICAL, product.type)
    }

    @Test
    fun deleted() {
        val response = rest.getForEntity("/v1/products/199", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun notFound() {
        val response = rest.getForEntity("/v1/products/99999", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.getForEntity("/v1/products/200", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, response.body?.error?.code)
    }
}
