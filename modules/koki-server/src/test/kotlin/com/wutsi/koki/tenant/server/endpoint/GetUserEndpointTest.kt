package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.GetUserResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GetUserEndpoint.sql"])
class GetUserEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/users/11", GetUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val user = result.body!!.user
        assertEquals(11L, user.id)
        assertEquals("Ray Sponsible", user.displayName)
        assertEquals("ray.sponsible@gmail.com", user.email)

        assertEquals(3, user.roles.size)

        assertEquals(10, user.roles[0].id)
        assertEquals("admin", user.roles[0].name)

        assertEquals(11, user.roles[1].id)
        assertEquals("writer", user.roles[1].name)

        assertEquals(12, user.roles[2].id)
        assertEquals("reader", user.roles[2].name)
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/users/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `user of another tenant`() {
        val result = rest.getForEntity("/v1/users/22", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }
}
