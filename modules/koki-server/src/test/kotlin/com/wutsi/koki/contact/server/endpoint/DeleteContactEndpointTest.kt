package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.contact.server.dao.ContactRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/contact/DeleteContactEndpoint.sql"])
class DeleteContactEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ContactRepository

    @Test
    fun delete() {
        rest.delete("/v1/contacts/100")

        val contactId = 100L
        val contact = dao.findById(contactId).get()
        assertEquals(USER_ID, contact.deletedById)
        assertTrue(contact.deleted)
        assertNotNull(contact.deletedById)
    }
}
