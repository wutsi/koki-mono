package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.product.server.dao.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/product/DeleteProductEndpoint.sql"])
class DeleteProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ProductRepository

    @Test
    fun delete() {
        rest.delete("/v1/products/100")

        val product = dao.findById(100L).get()
        assertEquals(true, product.deleted)
        assertEquals(USER_ID, product.deletedById)
        assertNotNull(product.deletedAt)
    }
}
