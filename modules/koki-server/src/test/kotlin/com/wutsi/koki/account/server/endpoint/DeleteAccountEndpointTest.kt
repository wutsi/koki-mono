package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.account.server.dao.AccountRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/account/DeleteAccountEndpoint.sql"])
class DeleteAccountEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AccountRepository

    @Test
    fun delete() {
        rest.delete("/v1/accounts/1000")

        val account = dao.findById(1000L).get()
        assertTrue(account.deleted)
        assertNotNull(account.deletedAt)
        assertEquals(USER_ID, account.deletedById)
        assertEquals(true, account.email.endsWith("-info@inc.com"))
    }

    @Test
    fun `account with contact`() {
        rest.delete("/v1/accounts/1100")

        val account = dao.findById(1100L).get()
        assertFalse(account.deleted)
        assertNull(account.deletedAt)
        assertNull(account.deletedById)
    }
}
