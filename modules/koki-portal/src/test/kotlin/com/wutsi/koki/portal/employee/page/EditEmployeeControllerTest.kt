package com.wutsi.koki.portal.employee.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.EmployeeFixtures.employee
import com.wutsi.koki.employee.dto.EmployeeStatus
import com.wutsi.koki.employee.dto.UpdateEmployeeRequest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class EditEmployeeControllerTest : AbstractPageControllerTest() {
    @Test
    fun update() {
        navigateTo("/employees/${employee.userId}/edit")

        assertCurrentPageIs(PageName.EMPLOYEE_EDIT)

        input("#jobTitle", "Director of Technology")
        select("#employeeTypeId", 2)
        select("#status", 2)
        scrollToBottom()
        input("#hourlyWage", "60")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<UpdateEmployeeRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/employees/${employee.userId}"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("Director of Technology", request.firstValue.jobTitle)
        assertEquals(112L, request.firstValue.employeeTypeId)
        assertEquals(EmployeeStatus.INACTIVE, request.firstValue.status)
        assertEquals(60.0, request.firstValue.hourlyWage)

        assertCurrentPageIs(PageName.EMPLOYEE_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun cancel() {
        navigateTo("/employees/${employee.userId}/edit")

        assertCurrentPageIs(PageName.EMPLOYEE_EDIT)

        input("#jobTitle", "Director of Technology")
        select("#status", 2)
        input("#hourlyWage", "60")
        scrollToBottom()
        click(".btn-cancel")

        assertCurrentPageIs(PageName.EMPLOYEE_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(),
            any<UpdateEmployeeRequest>(),
            eq(Any::class.java)
        )

        navigateTo("/employees/${employee.userId}/edit")

        assertCurrentPageIs(PageName.EMPLOYEE_EDIT)

        input("#jobTitle", "Director of Technology")
        select("#employeeTypeId", 2)
        select("#status", 2)
        scrollToBottom()
        input("#hourlyWage", "60")
        click("button[type=submit]", 1000)

        assertCurrentPageIs(PageName.EMPLOYEE_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/employees/${employee.userId}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `edit - without permission employee-manage`() {
        setUpUserWithoutPermissions(listOf("employee:manage"))

        navigateTo("/employees/${employee.userId}/edit")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
