package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.tenant.dto.SetRoleListRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SetUserRoleListEndpoint.sql"])
class SetUserRoleListEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    protected lateinit var ds: DataSource

    private fun roleCount(userId: Long): Int {
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery("SELECT count(*) FROM T_USER_ROLE where user_fk=$userId")
                rs.use {
                    if (rs.next()) {
                        return rs.getInt(1)
                    }
                }
            }
        }
        return -1
    }

    @Test
    fun set() {
        val request = SetRoleListRequest(roleIds = listOf(10L, 11L, 12L))
        val result = rest.postForEntity("/v1/users/11/roles", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(3, roleCount(11))
    }

    @Test
    fun reset() {
        val request = SetRoleListRequest(roleIds = listOf())
        val result = rest.postForEntity("/v1/users/12/roles", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(0, roleCount(12))
    }
}
