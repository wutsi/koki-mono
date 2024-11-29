package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SaveConfigurationResponse
import com.wutsi.koki.tenant.server.dao.ConfigurationRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SaveConfigurationEndpoint.sql"])
class SaveConfigurationEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ConfigurationRepository

    @Test
    fun update() {
        val request = SaveConfigurationRequest(
            values = mapOf("update" to "updated-value")
        )
        val result = rest.postForEntity("/v1/configurations", request, SaveConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(1, result.body?.updated)
        assertEquals(0, result.body?.added)
        assertEquals(0, result.body?.deleted)

        val config = dao.findByNameIgnoreCaseAndTenantId("update", TENANT_ID)
        assertEquals("updated-value", config?.value)
    }

    @Test
    fun delete() {
        val request = SaveConfigurationRequest(
            values = mapOf("delete" to "")
        )
        val result = rest.postForEntity("/v1/configurations", request, SaveConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(0, result.body?.updated)
        assertEquals(0, result.body?.added)
        assertEquals(1, result.body?.deleted)

        val config = dao.findByNameIgnoreCaseAndTenantId("delete", TENANT_ID)
        assertNull(config)
    }

    @Test
    fun add() {
        val request = SaveConfigurationRequest(
            values = mapOf("new" to "hello")
        )
        val result = rest.postForEntity("/v1/configurations", request, SaveConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(0, result.body?.updated)
        assertEquals(1, result.body?.added)
        assertEquals(0, result.body?.deleted)

        val config = dao.findByNameIgnoreCaseAndTenantId("new", TENANT_ID)
        assertEquals("hello", config?.value)
    }

    @Test
    fun `no values`() {
        val request = SaveConfigurationRequest(
            values = emptyMap()
        )
        val result = rest.postForEntity("/v1/configurations", request, SaveConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(0, result.body?.updated)
        assertEquals(0, result.body?.added)
        assertEquals(0, result.body?.deleted)
    }

    @Test
    fun `save batch`() {
        val request = SaveConfigurationRequest(
            values = mapOf(
                "batch-0" to "0",
                "batch-1" to "1",
                "batch-2" to "",
                "batch-3" to "3"
            )
        )
        val result = rest.postForEntity("/v1/configurations", request, SaveConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(2, result.body?.updated)
        assertEquals(1, result.body?.added)
        assertEquals(1, result.body?.deleted)

        assertEquals("0", dao.findByNameIgnoreCaseAndTenantId("batch-0", TENANT_ID)?.value)
        assertEquals("1", dao.findByNameIgnoreCaseAndTenantId("batch-1", TENANT_ID)?.value)
        assertNull(dao.findByNameIgnoreCaseAndTenantId("batch-2", TENANT_ID))
        assertEquals("3", dao.findByNameIgnoreCaseAndTenantId("batch-3", TENANT_ID)?.value)
    }
}
