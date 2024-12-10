package com.wutsi.koki.portal.page.settings.script

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ScriptFixtures.scripts
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.script.dto.SearchScriptResponse
import kotlin.test.Test

class ListScriptControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/scripts")
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_LIST)

        assertElementCount("tr.script", scripts.size)
        assertElementNotPresent(".empty")
    }

    @Test
    fun empty() {
        doReturn(SearchScriptResponse()).whenever(kokiScripts)
            .scripts(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/settings/scripts")
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_LIST)

        assertElementNotPresent("tr.script")
        assertElementPresent(".empty")
    }

    @Test
    fun `listview to create`() {
        navigateTo("/settings/scripts")
        click(".widget-toolbar .btn-create")

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_CREATE)
    }

    @Test
    fun `listview to show`() {
        navigateTo("/settings/scripts")
        click("tr.script .btn-view")

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT)
    }

    @Test
    fun `listview to edit`() {
        navigateTo("/settings/scripts")
        click("tr.script .btn-edit")

        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_EDIT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/scripts")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
