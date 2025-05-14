package com.wutsi.koki.portal.employee.page

import com.wutsi.koki.EmployeeFixtures.employee
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.NoteFixtures
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class EmployeeControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/employees/${employee.userId}")
        assertCurrentPageIs(PageName.EMPLOYEE)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/employees/${employee.userId}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun files() {
        navigateTo("/employees/${employee.userId}?tab=file")

        Thread.sleep(1000)
        assertElementCount(".tab-files tr.file", FileFixtures.files.size)
    }

    @Test
    fun notes() {
        navigateTo("/employees/${employee.userId}?tab=note")

        Thread.sleep(1000)
        assertElementCount(".tab-notes .note", NoteFixtures.notes.size)
    }

    @Test
    fun `show - without permission employee`() {
        setUpUserWithoutPermissions(listOf("employee"))

        navigateTo("/employees/${employee.userId}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `show - without permission employee-manage`() {
        setUpUserWithoutPermissions(listOf("employee:manage"))

        navigateTo("/employees/${employee.userId}")
        assertElementNotPresent(".employee-summary .btn-edit")
    }

    @Test
    fun `show - without permission employee-delete`() {
        setUpUserWithoutPermissions(listOf("employee:delete"))

        navigateTo("/employees/${employee.userId}")
        assertElementNotPresent(".employee-summary .btn-delete")
    }
}
