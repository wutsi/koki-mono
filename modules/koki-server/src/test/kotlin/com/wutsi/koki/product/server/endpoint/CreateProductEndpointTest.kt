package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.product.dto.CreateProductRequest
import com.wutsi.koki.product.dto.CreateProductResponse
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.server.dao.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql"])
class CreateProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ProductRepository

    private val request = CreateProductRequest(
        name = "Lunettes XXX",
        code = "XXX",
        description = "Lunettes futuristes",
        active = true,
        type = ProductType.PHYSICAL,
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
    }
}
