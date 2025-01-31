package com.wutsi.koki.employee.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.dto.GetEmployeeResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/employee/GetEmployeeEndpoint.sql"])
class GetEmployeeEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/employees/100", GetEmployeeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val employee = response.body!!.employee
        assertEquals("Ray", employee.firstName)
        assertEquals("Sponsible", employee.lastName)
        assertEquals("Director of Tech", employee.jobTitle)
        assertEquals(10000.0, employee.hourlyWage)
        assertEquals("XAF", employee.currency)
        assertEquals(EmployeeStatus.ACTIVE, employee.status)
    }

    @Test
    fun notFound() {
        val response = rest.getForEntity("/v1/employees/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, response.body!!.error.code)
    }

    @Test
    fun deleted() {
        val response = rest.getForEntity("/v1/employees/199", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, response.body!!.error.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.getForEntity("/v1/employees/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, response.body!!.error.code)
    }
}
