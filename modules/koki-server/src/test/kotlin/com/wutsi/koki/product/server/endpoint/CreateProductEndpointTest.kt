package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.product.dto.CreateProductRequest
import com.wutsi.koki.product.dto.CreateProductResponse
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.server.dao.PriceRepository
import com.wutsi.koki.product.server.dao.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/product/CreateProductEndpoint.sql"])
class CreateProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ProductRepository

    @Autowired
    private lateinit var priceDao: PriceRepository

    private val request = CreateProductRequest(
        name = "Lunettes XXX",
        code = "XXX",
        description = "Lunettes futuristes",
        active = true,
        type = ProductType.SERVICE,
        unitId = 11,
        quantity = 1,
        categoryId = 111L
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/products", request, CreateProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = dao.findById(response.body!!.productId).get()
        assertEquals(TENANT_ID, product.tenantId)
        assertEquals(request.name, product.name)
        assertEquals(request.description, product.description)
        assertEquals(request.code, product.code)
        assertEquals(request.active, product.active)
        assertEquals(request.type, product.type)
        assertEquals(false, product.deleted)
        assertEquals(USER_ID, product.createdById)
        assertEquals(USER_ID, product.modifiedById)
        assertEquals(request.categoryId, product.categoryId)
        assertEquals(request.quantity, product.serviceDetails?.quantity)
        assertEquals(request.unitId, product.serviceDetails?.unitId)

        val prices = priceDao.findByProductId(product.id!!)
        assertEquals(0, prices.size)
    }

    @Test
    fun `create with unit price`() {
        val xrequest = request.copy(
            unitPrice = 150.0,
            currency = "CAD"
        )
        val response = rest.postForEntity("/v1/products", xrequest, CreateProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = dao.findById(response.body!!.productId).get()
        assertEquals(TENANT_ID, product.tenantId)
        assertEquals(request.name, product.name)
        assertEquals(request.description, product.description)
        assertEquals(request.code, product.code)
        assertEquals(request.active, product.active)
        assertEquals(request.type, product.type)
        assertEquals(false, product.deleted)
        assertEquals(USER_ID, product.createdById)
        assertEquals(USER_ID, product.modifiedById)
        assertEquals(request.categoryId, product.categoryId)
        assertEquals(request.quantity, product.serviceDetails?.quantity)
        assertEquals(request.unitId, product.serviceDetails?.unitId)

        val prices = priceDao.findByProductId(product.id!!)
        assertEquals(1, prices.size)
        assertEquals(TENANT_ID, prices[0].tenantId)
        assertEquals(product.id, prices[0].productId)
        assertEquals(xrequest.unitPrice, prices[0].amount)
        assertEquals(xrequest.currency, prices[0].currency)
        assertEquals(true, prices[0].active)
        assertEquals(null, prices[0].startAt)
        assertEquals(null, prices[0].endAt)
    }

    @Test
    fun `create with unit price without currency`() {
        val xrequest = request.copy(
            unitPrice = 150.0,
            currency = ""
        )
        val response = rest.postForEntity("/v1/products", xrequest, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorCode.PRICE_CURRENCY_MISSING, response.body?.error?.code)
    }
}
