package com.wutsi.koki.price.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.product.dto.GetPriceResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/product/GetPriceEndpoint.sql"])
class GetPriceEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/prices/100", GetPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val price = response.body!!.price
        assertEquals(100L, price.productId)
        assertEquals("P1", price.name)
        assertEquals(1000.0, price.amount)
        assertEquals("CAD", price.currency)
        assertEquals(true, price.active)
        assertEquals("2020-10-11", fmt.format(price.startAt))
        assertEquals("2021-11-11", fmt.format(price.endAt))
    }

    @Test
    fun notFound() {
        val response = rest.getForEntity("/v1/prices/999999", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRICE_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.getForEntity("/v1/prices/200", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRICE_NOT_FOUND, response.body?.error?.code)
    }
}
