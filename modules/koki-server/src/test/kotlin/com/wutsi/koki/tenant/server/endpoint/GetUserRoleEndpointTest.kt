package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GetUserRoleEndpoint.sql"])
class GetUserRoleEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun search() {
        val result = rest.getForEntity("/v1/users/11/roles", SearchRoleResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val roles = result.body!!.roles
        assertEquals(2, roles.size)

        assertEquals(10L, roles[0].id)
        assertEquals("admin", roles[0].name)

        assertEquals(11L, roles[1].id)
        assertEquals("writer", roles[1].name)
    }

    @Test
    fun `search roles of user's from another tenant`() {
        val result = rest.getForEntity("/v1/users/22/roles", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body!!.error.code)
    }
}
