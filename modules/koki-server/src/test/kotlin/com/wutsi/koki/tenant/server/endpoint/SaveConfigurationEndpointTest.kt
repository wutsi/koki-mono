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
import kotlin.test.assertTrue

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

        val configs = dao.findByTenantIdAndNameIn(TENANT_ID, listOf("update"))
        assertEquals("updated-value", configs[0].value)
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

        val configs = dao.findByTenantIdAndNameIn(TENANT_ID, listOf("delete"))
        assertTrue(configs.isEmpty())
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

        val configs = dao.findByTenantIdAndNameIn(TENANT_ID, listOf("new"))
        assertEquals("hello", configs[0].value)
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
    fun `save another tenant config`() {
        val request = SaveConfigurationRequest(
            values = mapOf("other-tenant" to "hello")
        )
        val result = rest.postForEntity("/v1/configurations", request, SaveConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(0, result.body?.updated)
        assertEquals(0, result.body?.added)
        assertEquals(0, result.body?.deleted)

        assertTrue(dao.findByTenantIdAndNameIn(TENANT_ID, listOf("other-tenant")).isEmpty())
        assertEquals("aa1", dao.findByTenantIdAndNameIn(2L, listOf("other-tenant"))[0].value)
    }

    @Test
    fun `save batch`() {
        val request = SaveConfigurationRequest(
            values = mapOf("batch-0" to "0", "batch-1" to "1", "batch-2" to "", "batch-3" to "3")
        )
        val result = rest.postForEntity("/v1/configurations", request, SaveConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(2, result.body?.updated)
        assertEquals(1, result.body?.added)
        assertEquals(1, result.body?.deleted)

        assertEquals("0", dao.findByTenantIdAndNameIn(TENANT_ID, listOf("batch-0"))[0].value)
        assertEquals("1", dao.findByTenantIdAndNameIn(TENANT_ID, listOf("batch-1"))[0].value)
        assertTrue(dao.findByTenantIdAndNameIn(TENANT_ID, listOf("batch-2")).isEmpty())
        assertEquals("3", dao.findByTenantIdAndNameIn(TENANT_ID, listOf("batch-3"))[0].value)
    }
}
