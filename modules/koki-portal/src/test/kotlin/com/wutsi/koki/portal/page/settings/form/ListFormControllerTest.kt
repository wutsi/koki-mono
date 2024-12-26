package com.wutsi.koki.portal.page.settings.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FormFixtures.forms
import com.wutsi.koki.form.dto.Form
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class ListFormControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/forms")
        assertCurrentPageIs(PageName.SETTINGS_FORM_LIST)

        assertElementCount("tr.form", forms.size)
        assertElementNotPresent(".empty")
    }

    @Test
    fun empty() {
        doReturn(SearchFormResponse()).whenever(kokiForms)
            .forms(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/settings/forms")
        assertCurrentPageIs(PageName.SETTINGS_FORM_LIST)

        assertElementNotPresent("tr.form")
        assertElementPresent(".empty")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/forms")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun view() {
        val form = Form(
            id = "1",
            name = "M-001",
            title = "Message #1",
            active = true,
            content = FormContent(),
        )
        doReturn(GetFormResponse(form)).whenever(kokiForms).form(any())

        navigateTo("/settings/forms")
        click("tr.form .btn-view")
        assertCurrentPageIs(PageName.SETTINGS_FORM)
    }

    @Test
    fun edit() {
        val form = Form(
            id = "1",
            name = "M-001",
            title = "Message #1",
            active = true,
            content = FormContent(),
        )
        doReturn(GetFormResponse(form)).whenever(kokiForms).form(any())

        navigateTo("/settings/forms")
        click("tr.form .btn-edit")
        assertCurrentPageIs(PageName.SETTINGS_FORM_EDIT)
    }

    @Test
    fun create() {
        navigateTo("/settings/forms")
        click(".btn-create")
        assertCurrentPageIs(PageName.SETTINGS_FORM_CREATE)
    }

    @Test
    fun preview() {
        val form = Form(
            id = "1",
            name = "M-001",
            title = "Message #1",
            active = true,
            content = FormContent(),
        )
        doReturn(GetFormResponse(form)).whenever(kokiForms).form(any())

        navigateTo("/settings/forms")
        click(".btn-preview")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.FORM)
    }
}
