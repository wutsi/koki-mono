package com.wutsi.koki.price.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.product.dto.CreatePriceResponse
import com.wutsi.koki.product.dto.UpdatePriceRequest
import com.wutsi.koki.product.server.dao.PriceRepository
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/product/UpdatePriceEndpoint.sql"])
class UpdatePriceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: PriceRepository

    private val request = UpdatePriceRequest(
        name = "Default",
        amount = 555.0,
        currency = "CAD",
        startAt = DateUtils.addDays(Date(), 1),
        endAt = DateUtils.addDays(Date(), 10),
        active = true,
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/prices/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val price = dao.findById(100L).get()
        assertEquals(TENANT_ID, price.tenantId)
        assertEquals(request.name, price.name)
        assertEquals(request.amount, price.amount)
        assertEquals(request.currency, price.currency)
        assertEquals(request.active, price.active)
        assertEquals(fmt.format(request.startAt), fmt.format(price.startAt))
        assertEquals(fmt.format(request.endAt), fmt.format(price.endAt))
        assertEquals(USER_ID, price.modifiedById)
    }

    @Test
    fun notFound() {
        val response = rest.postForEntity("/v1/prices/999999", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRICE_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.postForEntity("/v1/prices/200", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRICE_NOT_FOUND, response.body?.error?.code)
    }
}
