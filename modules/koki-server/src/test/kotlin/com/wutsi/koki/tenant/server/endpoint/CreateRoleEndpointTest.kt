package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.CreateRoleRequest
import com.wutsi.koki.tenant.dto.CreateRoleResponse
import com.wutsi.koki.tenant.server.dao.RoleRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/CreateRoleEndpoint.sql"])
class CreateRoleEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoleRepository

    @Autowired
    protected lateinit var ds: DataSource

    private fun getPermissionIds(roleId: Long): List<Long> {
        val result = mutableListOf<Long>()
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery("SELECT permission_fk FROM T_ROLE_PERMISSION where role_fk=$roleId")
                rs.use {
                    while (rs.next()) {
                        result.add(rs.getLong(1))
                    }
                }
            }
        }
        return result
    }

    @Test
    fun create() {
        val request = CreateRoleRequest(
            name = "employee",
            title = "Employee",
            description = "Role for all employees",
            active = true,
            permissionIds = listOf(101, 201, 301)
        )

        val result = rest.postForEntity("/v1/roles", request, CreateRoleResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val roleId = result.body!!.roleId
        val role = dao.findById(roleId).get()
        assertEquals(request.name, role.name)
        assertEquals(request.title, role.title)
        assertEquals(request.description, role.description)
        assertEquals(request.active, role.active)
        assertEquals(TENANT_ID, role.tenantId)
        assertEquals(USER_ID, role.createdById)
        assertEquals(USER_ID, role.modifiedById)
        assertFalse(role.deleted)
        assertNull(role.deletedById)
        assertNull(role.deletedAt)
        assertEquals(request.permissionIds, getPermissionIds(roleId))
    }

    @Test
    fun duplicate() {
        val request = CreateRoleRequest(
            name = "admin",
            title = "Employee",
            description = "Role for all employees",
            active = true,
        )

        val result = rest.postForEntity("/v1/roles", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.ROLE_DUPLICATE_NAME, result.body!!.error.code)
    }
}
