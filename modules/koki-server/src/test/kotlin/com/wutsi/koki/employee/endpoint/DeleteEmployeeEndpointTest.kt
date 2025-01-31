package com.wutsi.koki.employee.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.employee.server.dao.EmployeeRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/employee/DeleteEmployeeEndpoint.sql"])
class DeleteEmployeeEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: EmployeeRepository

    @Test
    fun delete() {
        rest.delete("/v1/employees/100")

        val employee = dao.findById(100).get()
        assertTrue(employee.deleted)
        assertNotNull(employee.deletedAt)
        assertEquals(USER_ID, employee.deletedById)
    }
}
