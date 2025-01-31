package com.wutsi.koki.employee.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.employee.dto.CreateEmployeeRequest
import com.wutsi.koki.employee.dto.CreateEmployeeResponse
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.server.dao.EmployeeRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql"])
class CreateEmployeeEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: EmployeeRepository

    private val request = CreateEmployeeRequest(
        firstName = "Ray",
        lastName = "Sponsible",
        jobTitle = "Director of Technology",
        hourlyWage = 10000.0,
        currency = "XAF",
        status = EmployeeStatus.ACTIVE,
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/employees", request, CreateEmployeeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val employeeId = response.body!!.employeeId
        val employee = dao.findById(employeeId).get()

        assertEquals(request.firstName, employee.firstName)
        assertEquals(request.lastName, employee.lastName)
        assertEquals(request.jobTitle, employee.jobTitle)
        assertEquals(request.hourlyWage, employee.hourlyWage)
        assertEquals(request.currency, employee.currency)
        assertEquals(request.status, employee.status)
        assertEquals(USER_ID, employee.createdById)
        assertEquals(USER_ID, employee.modifiedById)
    }
}
