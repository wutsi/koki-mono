package com.wutsi.koki.employee.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.employee.dto.SearchEmployeeResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/employee/SearchEmployeeEndpoint.sql"])
class SearchEmployeeEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/employees", SearchEmployeeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val employees = response.body!!.employees
        assertEquals(4, employees.size)
    }

    @Test
    fun `by status`() {
        val response = rest.getForEntity("/v1/employees?status=ACTIVE", SearchEmployeeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val employees = response.body!!.employees
        assertEquals(3, employees.size)
        assertEquals(listOf(100L, 110L, 130L), employees.map { employee -> employee.userId }.sorted())
    }

    @Test
    fun `by ids`() {
        val response =
            rest.getForEntity("/v1/employees?id=100&id=110&id=130", SearchEmployeeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val employees = response.body!!.employees
        assertEquals(3, employees.size)
        assertEquals(listOf(100L, 110L, 130L), employees.map { employee -> employee.userId }.sorted())
    }

    @Test
    fun deleted() {
        val response = rest.getForEntity("/v1/employees?id=199", SearchEmployeeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val employees = response.body!!.employees
        assertEquals(0, employees.size)
    }

    @Test
    fun anotherTenant() {
        val response = rest.getForEntity("/v1/employees?id=200", SearchEmployeeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val employees = response.body!!.employees
        assertEquals(0, employees.size)
    }
}
