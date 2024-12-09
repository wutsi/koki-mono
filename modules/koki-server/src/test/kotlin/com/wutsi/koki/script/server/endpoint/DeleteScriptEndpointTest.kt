package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.script.server.dao.ScriptRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/script/DeleteScriptEndpoint.sql"])
class DeleteScriptEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ScriptRepository

    @Test
    fun delete() {
        rest.delete("/v1/scripts/100")

        val script = dao.findById("100").get()
        assertTrue(script.deleted)
        assertNotNull(script.deletedAt)
        assertTrue(script.name.startsWith("##-S-100"))
    }

    @Test
    fun `another tenant`() {
        rest.delete("/v1/scripts/200")

        val script = dao.findById("200").get()
        assertFalse(script.deleted)
        assertTrue(script.name.startsWith("S-200"))
    }

    @Test
    fun `in use`() {
        rest.delete("/v1/forms/110")

        val script = dao.findById("110").get()
        assertFalse(script.deleted)
        assertNull(script.deletedAt)
    }
}
