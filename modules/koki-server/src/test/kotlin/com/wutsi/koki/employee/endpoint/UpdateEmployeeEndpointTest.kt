package com.wutsi.koki.employee.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.dto.UpdateEmployeeRequest
import com.wutsi.koki.employee.server.dao.EmployeeRepository
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/employee/UpdateEmployeeEndpoint.sql"])
class UpdateEmployeeEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: EmployeeRepository

    private val request = UpdateEmployeeRequest(
        jobTitle = "Director of Technology",
        hourlyWage = 10000.0,
        currency = "XAF",
        status = EmployeeStatus.ACTIVE,
        hiredAt = DateUtils.addDays(Date(), 7),
        terminatedAt = DateUtils.addDays(Date(), 300),
        employeeTypeId = 777L,
    )

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/employees/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val employee = dao.findById(100L).get()
        assertEquals(request.jobTitle, employee.jobTitle)
        assertEquals(request.hourlyWage, employee.hourlyWage)
        assertEquals(request.currency, employee.currency)
        assertEquals(request.status, employee.status)
        assertEquals(fmt.format(request.hiredAt), fmt.format(employee.hiredAt))
        assertEquals(fmt.format(request.terminatedAt), fmt.format(employee.terminatedAt))
        assertEquals(USER_ID, employee.modifiedById)
        assertEquals(request.employeeTypeId, employee.employeeTypeId)
    }

    @Test
    fun notFound() {
        val response = rest.postForEntity("/v1/employees/999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, response.body!!.error.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.postForEntity("/v1/employees/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, response.body!!.error.code)
    }
}
