package com.wutsi.koki.portal.employee.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.employee.dto.CreateEmployeeRequest
import com.wutsi.koki.employee.dto.CreateEmployeeResponse
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class CreateEmployeeControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/employees/create")

        assertCurrentPageIs(PageName.EMPLOYEE_CREATE)

        input("#email", "ray.sponsible@gmail.com")
        select("#employeeTypeId", 2)
        input("#jobTitle", "Director of Technology")
        select("#status", 2)
        scrollToBottom()
        input("#hourlyWage", "60")
        click("button[type=submit]")

        val request = argumentCaptor<CreateEmployeeRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/employees"),
            request.capture(),
            eq(CreateEmployeeResponse::class.java)
        )
        assertEquals("ray.sponsible@gmail.com", request.firstValue.email)
        assertEquals(112L, request.firstValue.employeeTypeId)
        assertEquals("Director of Technology", request.firstValue.jobTitle)
        assertEquals(EmployeeStatus.INACTIVE, request.firstValue.status)
        assertEquals(60.0, request.firstValue.hourlyWage)

        assertCurrentPageIs(PageName.EMPLOYEE_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun cancel() {
        navigateTo("/employees/create")

        assertCurrentPageIs(PageName.EMPLOYEE_CREATE)

        input("#email", "ray.sponsible@gmail.com")
        select("#employeeTypeId", 2)
        input("#jobTitle", "Director of Technology")
        select("#status", 2)
        scrollToBottom()
        input("#hourlyWage", "60")
        click(".btn-cancel")

        assertCurrentPageIs(PageName.EMPLOYEE_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(),
            any<CreateEmployeeRequest>(),
            eq(CreateEmployeeResponse::class.java)
        )

        navigateTo("/employees/create")

        assertCurrentPageIs(PageName.EMPLOYEE_CREATE)

        input("#email", "ray.sponsible@gmail.com")
        select("#employeeTypeId", 2)
        input("#jobTitle", "Director of Technology")
        select("#status", 2)
        scrollToBottom()
        input("#hourlyWage", "60")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.EMPLOYEE_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/employees/create")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `create - without permission employee-manage`() {
        setUpUserWithoutPermissions(listOf("employee:manage"))

        navigateTo("/employees/create")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
