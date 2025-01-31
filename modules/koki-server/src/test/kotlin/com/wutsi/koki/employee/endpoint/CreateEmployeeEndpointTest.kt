package com.wutsi.koki.employee.endpoint

import com.ibm.icu.text.SimpleDateFormat
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.employee.dto.CreateEmployeeRequest
import com.wutsi.koki.employee.dto.CreateEmployeeResponse
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.server.dao.EmployeeRepository
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.dao.UserRepository
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/employee/CreateEmployeeEndpoint.sql"])
class CreateEmployeeEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: EmployeeRepository

    @Autowired
    private lateinit var userDao: UserRepository

    private val request = CreateEmployeeRequest(
        userId = 111L,
        jobTitle = "Director of Technology",
        hourlyWage = 10000.0,
        currency = "XAF",
        status = EmployeeStatus.ACTIVE,
        hiredAt = DateUtils.addDays(Date(), 7),
        terminatedAt = DateUtils.addDays(Date(), 300)
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/employees", request, CreateEmployeeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val employeeId = response.body!!.employeeId
        val employee = dao.findById(employeeId).get()

        assertEquals(request.userId, employee.id)
        assertEquals(request.jobTitle, employee.jobTitle)
        assertEquals(request.hourlyWage, employee.hourlyWage)
        assertEquals(request.currency, employee.currency)
        assertEquals(request.status, employee.status)
        assertEquals(fmt.format(request.hiredAt), fmt.format(employee.hiredAt))
        assertEquals(fmt.format(request.terminatedAt), fmt.format(employee.terminatedAt))
        assertEquals(USER_ID, employee.createdById)
        assertEquals(USER_ID, employee.modifiedById)

        val user = userDao.findById(request.userId).get()
        assertEquals(UserType.EMPLOYEE, user.type)
    }

    @Test
    fun `already assigned`() {
        val response = rest.postForEntity("/v1/employees", request.copy(userId = 155), ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.USER_ALREADY_ASSIGNED, response.body!!.error.code)
    }
}
