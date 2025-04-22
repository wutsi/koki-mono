package com.wutsi.koki.portal.employee.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.EmployeeFixtures.employees
import com.wutsi.koki.employee.dto.EmployeeSummary
import com.wutsi.koki.employee.dto.SearchEmployeeResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListEmployeeControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/employees")

        assertCurrentPageIs(PageName.EMPLOYEE_LIST)
        assertElementCount("tr.employee", employees.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<EmployeeSummary>()
        repeat(20) {
            entries.add(employees[0].copy())
        }
        doReturn(
            ResponseEntity(
                SearchEmployeeResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchEmployeeResponse::class.java)
            )

        navigateTo("/employees")

        assertCurrentPageIs(PageName.EMPLOYEE_LIST)
        assertElementCount("tr.employee", entries.size)

        scrollToBottom()
        click("#employee-load-more a", 1000)
        assertElementCount("tr.employee", 2 * entries.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/employees")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun show() {
        navigateTo("/employees")
        click("tr.employee a")
        assertCurrentPageIs(PageName.EMPLOYEE)
    }

    @Test
    fun create() {
        navigateTo("/employees")
        click(".btn-create")
        assertCurrentPageIs(PageName.EMPLOYEE_CREATE)
    }

    @Test
    fun `list - without permission employee`() {
        setUpUserWithoutPermissions(listOf("employee"))

        navigateTo("/employees")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `list - without permission employee-manage`() {
        setUpUserWithoutPermissions(listOf("employee:manage"))

        navigateTo("/employees")

        assertCurrentPageIs(PageName.EMPLOYEE_LIST)
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-create")
    }
}
