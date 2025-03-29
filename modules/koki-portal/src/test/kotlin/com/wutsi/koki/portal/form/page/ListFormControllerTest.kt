package com.wutsi.koki.portal.form.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FormFixtures.forms
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListFormControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/forms")

        assertCurrentPageIs(PageName.FORM_LIST)
        assertElementCount("tr.form", forms.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<FormSummary>()
        repeat(20) {
            entries.add(forms[0].copy())
        }
        doReturn(
            ResponseEntity(
                SearchFormResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchFormResponse::class.java)
            )

        navigateTo("/forms")

        assertCurrentPageIs(PageName.FORM_LIST)
        assertElementCount("tr.form", entries.size)

        scrollToBottom()
        click("#form-load-more a", 1000)
        assertElementCount("tr.form", 2 * entries.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/forms")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun show() {
        navigateTo("/forms")
        click("tr.form a")
        assertCurrentPageIs(PageName.FORM)
    }

    @Test
    fun create() {
        navigateTo("/forms")
        click(".btn-create")
        assertCurrentPageIs(PageName.FORM_CREATE)
    }

    @Test
    fun `list - without permission form`() {
        setUpUserWithoutPermissions(listOf("form"))

        navigateTo("/forms")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `list - without permission form-manage`() {
        setUpUserWithoutPermissions(listOf("form:manage"))

        navigateTo("/forms")

        assertCurrentPageIs(PageName.FORM_LIST)
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-create")
    }
}
