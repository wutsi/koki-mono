package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.account.dto.SearchAccountResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/account/SearchAccountEndpoint.sql"])
class SearchAccountEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/accounts", SearchAccountResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accounts
        assertEquals(4, accounts.size)
    }

    @Test
    fun `by creator`() {
        val result = rest.getForEntity("/v1/accounts?created-by-id=11", SearchAccountResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accounts
        assertEquals(2, accounts.size)
        assertEquals(1000L, accounts[0].id)
        assertEquals(1001L, accounts[1].id)
    }

    @Test
    fun `by manager`() {
        val result = rest.getForEntity("/v1/accounts?managed-by-id=13", SearchAccountResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accounts
        assertEquals(3, accounts.size)
        assertEquals(1002L, accounts[0].id)
        assertEquals(1003L, accounts[1].id)
        assertEquals(1001L, accounts[2].id)
    }

    @Test
    fun `by name`() {
        val result = rest.getForEntity("/v1/accounts?q=inc", SearchAccountResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accounts
        assertEquals(3, accounts.size)
        assertEquals(1002L, accounts[0].id)
        assertEquals(1000L, accounts[1].id)
        assertEquals(1001L, accounts[2].id)
    }

    @Test
    fun `by email`() {
        val result = rest.getForEntity("/v1/accounts?q=info", SearchAccountResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accounts
        assertEquals(2, accounts.size)
        assertEquals(1000L, accounts[0].id)
        assertEquals(1001L, accounts[1].id)
    }

    @Test
    fun `by phone`() {
        val result = rest.getForEntity("/v1/accounts?q=758", SearchAccountResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accounts
        assertEquals(3, accounts.size)
        assertEquals(1002L, accounts[0].id)
        assertEquals(1000L, accounts[1].id)
        assertEquals(1001L, accounts[2].id)
    }

    @Test
    fun `by mobile`() {
        val result = rest.getForEntity("/v1/accounts?q=931", SearchAccountResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accounts
        assertEquals(2, accounts.size)
        assertEquals(1002L, accounts[0].id)
        assertEquals(1001L, accounts[1].id)
    }

    @Test
    fun `by account type`() {
        val result = rest.getForEntity("/v1/accounts?account-type-id=101", SearchAccountResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accounts
        assertEquals(2, accounts.size)
        assertEquals(1003L, accounts[0].id)
        assertEquals(1001L, accounts[1].id)
    }
}
