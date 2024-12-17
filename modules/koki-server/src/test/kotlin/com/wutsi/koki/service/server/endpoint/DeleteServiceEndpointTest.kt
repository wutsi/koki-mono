package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.service.server.dao.ServiceRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/service/DeleteServiceEndpoint.sql"])
class DeleteServiceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ServiceRepository

    @Test
    fun delete() {
        rest.delete("/v1/services/100")

        val service = dao.findById("100").get()
        assertEquals(true, service.deleted)
        assertNotNull(service.deletedAt)
    }

    @Test
    fun `another tenant`() {
        rest.delete("/v1/services/200")

        val service = dao.findById("100").get()
        assertEquals(false, service.deleted)
        assertNull(service.deletedAt)
    }

    @Test
    fun `in use`() {
        rest.delete("/v1/services/110")

        val script = dao.findById("110").get()
        assertFalse(script.deleted)
        assertNull(script.deletedAt)
    }
}
