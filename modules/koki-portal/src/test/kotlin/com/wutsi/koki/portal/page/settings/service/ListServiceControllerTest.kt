package com.wutsi.koki.portal.page.settings.service

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ServiceFixtures.services
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.service.dto.SearchServiceResponse
import kotlin.test.Test

class ListServiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/services")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_LIST)

        assertElementCount("tr.service", services.size)
        assertElementNotPresent(".empty")
    }

    @Test
    fun empty() {
        doReturn(SearchServiceResponse()).whenever(kokiServices)
            .services(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/settings/services")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_LIST)

        assertElementNotPresent("tr.service")
        assertElementPresent(".empty")
    }

    @Test
    fun create() {
        navigateTo("/settings/services")
        click(".widget-toolbar .btn-create")

        assertCurrentPageIs(PageName.SETTINGS_SERVICE_CREATE)
    }

    @Test
    fun show() {
        navigateTo("/settings/services")
        click("tr.service .btn-view")

        assertCurrentPageIs(PageName.SETTINGS_SERVICE)
    }

    @Test
    fun edit() {
        navigateTo("/settings/services")
        click("tr.service .btn-edit")

        assertCurrentPageIs(PageName.SETTINGS_SERVICE_EDIT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/services")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
