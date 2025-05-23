package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.tenant.dto.SearchUserResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SearchUserEndpoint.sql"])
class SearchUserEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun `search by ids`() {
        val result = rest.getForEntity("/v1/users?id=11&id=12&id=13", SearchUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users

        assertEquals(3, users.size)
        assertEquals(listOf(12L, 11L, 13L), users.map { it.id })
    }

    @Test
    fun `search by ids - never return user from other tenant`() {
        val result = rest.getForEntity("/v1/users?id=11&id=12&id=13&id=22", SearchUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users

        assertEquals(3, users.size)
        assertFalse(users.map { it.id }.contains(22L))
    }

    @Test
    fun `search display-name starts with`() {
        val result = rest.getForEntity("/v1/users?q=peter", SearchUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users

        assertEquals(3, users.size)

        assertEquals(17L, users[0].id)
        assertEquals("Peter Fonda", users[0].displayName)

        assertEquals(15L, users[1].id)
        assertEquals("Peter Pan", users[1].displayName)

        assertEquals(16L, users[2].id)
        assertEquals("Peter Parker", users[2].displayName)
    }

    @Test
    fun `search display-name contains ignore case`() {
        val result = rest.getForEntity("/v1/users?q=FONDA", SearchUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users

        assertEquals(2, users.size)

        assertEquals(18L, users[0].id)
        assertEquals("Henry Fonda", users[0].displayName)

        assertEquals(17L, users[1].id)
        assertEquals("Peter Fonda", users[1].displayName)
    }

    @Test
    fun `by role`() {
        val result = rest.getForEntity("/v1/users?role-id=11", SearchUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users

        assertEquals(2, users.size)

        assertEquals(12L, users[0].id)
        assertEquals(11L, users[1].id)
    }

    @Test
    fun `by permission`() {
        val result = rest.getForEntity("/v1/users?permission=module1:read", SearchUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users

        assertEquals(2, users.size)

        assertEquals(12L, users[0].id)
        assertEquals(11L, users[1].id)
    }

    @Test
    fun `by type`() {
        val result = rest.getForEntity("/v1/users?type=ACCOUNT", SearchUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users

        assertEquals(3, users.size)

        assertEquals(true, users.map { user -> user.id }.containsAll(listOf(14L, 16L, 18L)))
    }

    @Test
    fun `by status`() {
        val result = rest.getForEntity("/v1/users?status=TERMINATED", SearchUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users

        assertEquals(1, users.size)

        assertEquals(14L, users[0].id)
    }

    @Test
    fun all() {
        val result =
            rest.getForEntity("/v1/users?limit=2&offset=4", SearchUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val users = result.body!!.users

        assertEquals(2, users.size)
    }
}
