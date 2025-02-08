package com.wutsi.koki.price.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.product.dto.CreatePriceRequest
import com.wutsi.koki.product.dto.CreatePriceResponse
import com.wutsi.koki.product.server.dao.PriceRepository
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/product/CreatePriceEndpoint.sql"])
class CreatePriceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: PriceRepository

    private val request = CreatePriceRequest(
        name = "Default",
        accountTypeId = 111L,
        amount = 555.0,
        currency = "CAD",
        startAt = DateUtils.addDays(Date(), 1),
        endAt = DateUtils.addDays(Date(), 10),
        active = true,
        productId = 100L,
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/prices", request, CreatePriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val price = dao.findById(response.body!!.priceId).get()
        assertEquals(TENANT_ID, price.tenantId)
        assertEquals(request.productId, price.productId)
        assertEquals(request.accountTypeId, price.accountTypeId)
        assertEquals(request.name, price.name)
        assertEquals(request.amount, price.amount)
        assertEquals(request.currency, price.currency)
        assertEquals(request.active, price.active)
        assertEquals(fmt.format(request.startAt), fmt.format(price.startAt))
        assertEquals(fmt.format(request.endAt), fmt.format(price.endAt))
        assertEquals(false, price.deleted)
        assertEquals(USER_ID, price.createdById)
        assertEquals(USER_ID, price.modifiedById)
    }
}
