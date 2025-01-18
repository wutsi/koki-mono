package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tenant.dto.SetPermissionListRequest
import com.wutsi.koki.tenant.server.dao.RoleRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SetRolePermissionEndpoint.sql"])
class SetRolePermissionEndpointTest : AuthorizationAwareEndpointTest() {
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
    fun set() {
        val request = SetPermissionListRequest(
            permissionIds = listOf(101, 201, 301)
        )

        val result = rest.postForEntity("/v1/roles/10/permissions", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val role = dao.findById(10L).get()
        assertEquals(USER_ID, role.modifiedById)
        assertEquals(request.permissionIds, getPermissionIds(10L))
    }

    @Test
    fun reset() {
        val request = SetPermissionListRequest(
            permissionIds = listOf()
        )

        val result = rest.postForEntity("/v1/roles/20/permissions", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val role = dao.findById(20L).get()
        assertEquals(USER_ID, role.modifiedById)
        assertEquals(request.permissionIds, getPermissionIds(20L))
    }
}
