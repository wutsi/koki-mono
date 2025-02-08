package com.wutsi.koki.price.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.product.server.dao.PriceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/product/DeletePriceEndpoint.sql"])
class DeletePriceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: PriceRepository

    @Test
    fun create() {
        rest.delete("/v1/prices/100")

        val price = dao.findById(100L).get()
        assertEquals(true, price.deleted)
        assertEquals(USER_ID, price.deletedById)
        assertNotNull(price.deletedAt)
    }
}
