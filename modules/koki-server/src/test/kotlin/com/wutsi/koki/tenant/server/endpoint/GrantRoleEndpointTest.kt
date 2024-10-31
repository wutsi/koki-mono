package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.GrantRoleRequest
import com.wutsi.koki.tenant.server.dao.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.fail

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GrantUserRoleEndpoint.sql"])
class GrantUserRoleEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: UserRepository

    @Test
    fun grant() {
        val request = GrantRoleRequest(roleId = 11L)
        val result = rest.postForEntity("/v1/users/11/roles", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val user = dao.findById(11L).get()
        assertEquals(2, user.roles.size)
    }

    @Test
    fun `grant again`() {
        fail()
    }

    @Test
    fun `grant role to a user of another tenant`() {
        fail()
    }

    @Test
    fun `grant role of another tenant to a user`() {
        fail()
    }
}
