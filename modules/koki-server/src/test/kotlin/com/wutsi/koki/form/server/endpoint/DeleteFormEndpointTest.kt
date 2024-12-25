package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.form.server.dao.FormRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/form/DeleteFormEndpoint.sql"])
class DeleteFormEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FormRepository

    @Test
    fun delete() {
        rest.delete("/v1/forms/100")

        val message = dao.findById("100").get()
        assertTrue(message.deleted)
        assertNotNull(message.deletedAt)
    }

    @Test
    fun `in use`() {
        rest.delete("/v1/forms/110")

        val message = dao.findById("110").get()
        assertFalse(message.deleted)
        assertNull(message.deletedAt)
    }

    @Test
    fun `other tenant`() {
        rest.delete("/v1/forms/200")
    }
}
