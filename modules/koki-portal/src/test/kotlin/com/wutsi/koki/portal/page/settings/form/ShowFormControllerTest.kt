package com.wutsi.koki.portal.page.settings.form

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.FormFixtures.form
import com.wutsi.koki.FormFixtures.formSubmissions
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.form.dto.FormSubmissionSummary
import com.wutsi.koki.form.dto.GetFormResponse
import com.wutsi.koki.form.dto.SearchFormSubmissionResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import java.util.UUID
import kotlin.test.Test

class ShowFormControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetFormResponse(form)).whenever(kokiForms).form(any())
    }

    @Test
    fun show() {
        navigateTo("/settings/forms/${form.id}")
        assertCurrentPageIs(PageName.SETTINGS_FORM)
        assertElementNotPresent(".alert-danger")

        click("#pills-submissions-tab")
        waitForPresenceOf(".submissions-widget tr.submission")
        assertElementCount(".submissions-widget tr.submission", files.size)

        click("#pills-share-tab")
    }

    @Test
    fun share() {
        navigateTo("/settings/forms/${form.id}")

        click("#pills-share-tab")
        click("#form-share")

        val alert = driver.switchTo().alert()
        alert.accept()
    }

    @Test
    fun `load more submissions`() {
        var entries = mutableListOf<FormSubmissionSummary>()
        repeat(20) {
            entries.add(formSubmissions[0].copy(id = UUID.randomUUID().toString()))
        }
        doReturn(SearchFormSubmissionResponse(entries))
            .doReturn(SearchFormSubmissionResponse(formSubmissions))
            .whenever(kokiForms)
            .submissions(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/settings/forms/${form.id}")

        click("#pills-submissions-tab")
        waitForPresenceOf(".submissions-widget tr.submission")
        assertElementCount(".submissions-widget tr.submission", entries.size)

        scrollToBottom()
        click("#submission-load-more a", 1000)
        assertElementCount(".submissions-widget tr.submission", entries.size + formSubmissions.size)
    }

    @Test
    fun submission() {
        navigateTo("/settings/forms/${form.id}")
        assertCurrentPageIs(PageName.SETTINGS_FORM)
        assertElementNotPresent(".alert-danger")

        click("#pills-submissions-tab")
        waitForPresenceOf(".submissions-widget tr.submission")
        click(".submissions-widget tr.submission .btn-view", 1000)

        driver.switchTo().window(driver.getWindowHandles().toList()[1])
        assertCurrentPageIs(PageName.SETTINGS_FORM_SUBMISSION)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/forms/${form.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/settings/forms/${form.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiForms).delete(form.id)
        assertCurrentPageIs(PageName.SETTINGS_FORM_DELETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_FORM_LIST)
    }

    @Test
    fun `dismiss delete`() {
        navigateTo("/settings/forms/${form.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(kokiForms, never()).delete(any())
        assertCurrentPageIs(PageName.SETTINGS_FORM)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(kokiForms).delete(any())

        navigateTo("/settings/forms/${form.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.SETTINGS_FORM)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/settings/forms/${form.id}")
        click(".btn-edit")

        assertCurrentPageIs(PageName.SETTINGS_FORM_EDIT)
    }

    @Test
    fun preview() {
        navigateTo("/settings/forms/${form.id}")
        click(".btn-preview")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.FORM)
    }
}
