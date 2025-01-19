package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tenant.server.dao.RoleRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/DeleteRoleEndpoint.sql"])
class DeleteRoleEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoleRepository

    @Test
    fun delete() {
        rest.delete("/v1/roles/10")

        val role = dao.findById(10).get()
        assertEquals(USER_ID, role.deletedById)
        assertNotNull(role.deletedById)
        assertTrue(role.deleted)
    }

    @Test
    fun used() {
        rest.delete("/v1/roles/20")

        val role = dao.findById(20).get()
        assertNull(role.deletedById)
        assertNull(role.deletedById)
        assertFalse(role.deleted)
    }
}
