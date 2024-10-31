package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.tenant.dto.GrantRoleRequest
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GrantRoleEndpoint.sql"])
class GrantRoleEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun grant() {
        val request = GrantRoleRequest(roleId = 11L)
        val result = rest.postForEntity("/v1/users/11/roles", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val roles = rest.getForEntity("/v1/users/11/roles", SearchRoleResponse::class.java).body!!.roles
        assertEquals(2, roles.size)
    }

    @Test
    fun `grant again`() {
        val request = GrantRoleRequest(roleId = 10L)
        val result = rest.postForEntity("/v1/users/12/roles", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val roles = rest.getForEntity("/v1/users/12/roles", SearchRoleResponse::class.java).body!!.roles
        assertEquals(1, roles.size)
    }

    @Test
    fun `grant role of another tenant to a user`() {
        val request = GrantRoleRequest(roleId = 20L)
        val result = rest.postForEntity("/v1/users/11/roles", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.ROLE_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `grant role to a user of another tenant`() {
        val request = GrantRoleRequest(roleId = 10L)
        val result = rest.postForEntity("/v1/users/22/roles", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body!!.error.code)
    }
}
