package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.message.server.dao.MessageRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/message/DeleteMessageEndpoint.sql"])
class DeleteMessageEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: MessageRepository

    @Test
    fun delete() {
        rest.delete("/v1/messages/100")

        val message = dao.findById("100").get()
        assertTrue(message.deleted)
        assertNotNull(message.deletedAt)
    }

    @Test
    fun `in use`() {
        rest.delete("/v1/messages/110")

        val message = dao.findById("110").get()
        assertFalse(message.deleted)
        assertNull(message.deletedAt)
    }

    @Test
    fun `other tenant`() {
        rest.delete("/v1/messages/200")

        val message = dao.findById("200").get()
        assertFalse(message.deleted)
        assertNull(message.deletedAt)
    }
}
