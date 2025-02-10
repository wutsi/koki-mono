package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.dto.UpdateProductRequest
import com.wutsi.koki.product.server.dao.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/product/UpdateProductEndpoint.sql"])
class UpdateProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ProductRepository

    private val request = UpdateProductRequest(
        name = "Lunettes RayBand",
        code = "RAY123",
        description = "Lunettes futuristes",
        active = true,
        type = ProductType.SUBSCRIPTION,
        unitId = 11,
        quantity = 1,
    )

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/products/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = dao.findById(100L).get()
        assertEquals(request.name, product.name)
        assertEquals(request.description, product.description)
        assertEquals(request.code, product.code)
        assertEquals(request.active, product.active)
        assertEquals(request.type, product.type)
        assertEquals(USER_ID, product.modifiedById)
        assertEquals(request.quantity, product.serviceDetails?.quantity)
        assertEquals(request.unitId, product.serviceDetails?.unitId)
    }

    @Test
    fun deleted() {
        val response = rest.postForEntity("/v1/products/199", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun notFound() {
        val response = rest.postForEntity("/v1/products/999999", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.postForEntity("/v1/products/200", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, response.body?.error?.code)
    }
}
