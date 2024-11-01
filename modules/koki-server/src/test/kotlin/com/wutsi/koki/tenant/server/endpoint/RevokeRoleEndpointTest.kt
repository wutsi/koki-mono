package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/RevokeRoleEndpoint.sql"])
class RevokeRoleEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun revoke() {
        rest.delete("/v1/users/11/roles/10")

        val roles = rest.getForEntity("/v1/users/11/roles", SearchRoleResponse::class.java).body!!.roles
        assertEquals(1, roles.size)
    }

    @Test
    fun `revoke role never granted`() {
        rest.delete("/v1/users/12/roles/11")

        val roles = rest.getForEntity("/v1/users/12/roles", SearchRoleResponse::class.java).body!!.roles
        assertEquals(1, roles.size)
    }

    @Test
    fun `revoke role of another tenant`() {
        val response = rest.exchange("/v1/users/11/roles/20", HttpMethod.DELETE, null, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ROLE_NOT_FOUND, response.body!!.error.code)
    }

    @Test
    fun `revoke role from a user of another tenant`() {
        val response = rest.exchange("/v1/users/22/roles/10", HttpMethod.DELETE, null, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, response.body!!.error.code)
    }
}
