package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.account.dto.SearchAccountTypeResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/account/SearchAccountTypeEndpoint.sql"])
class SearchAccountTypeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/account-types", SearchAccountTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accountTypes
        assertEquals(4, accounts.size)
    }

    @Test
    fun `by name`() {
        val result = rest.getForEntity("/v1/account-types?name=T1&name=T2", SearchAccountTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accountTypes
        assertEquals(2, accounts.size)
        assertEquals(100L, accounts[0].id)
        assertEquals(101L, accounts[1].id)
    }

    @Test
    fun `by id`() {
        val result =
            rest.getForEntity("/v1/account-types?id=103&id=100&id=101", SearchAccountTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accountTypes
        assertEquals(3, accounts.size)
        assertEquals(100L, accounts[0].id)
        assertEquals(101L, accounts[1].id)
        assertEquals(103L, accounts[2].id)
    }

    @Test
    fun active() {
        val result = rest.getForEntity("/v1/account-types?active=false", SearchAccountTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accounts = result.body!!.accountTypes
        assertEquals(1, accounts.size)
        assertEquals(103L, accounts[0].id)
    }
}
