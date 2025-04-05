package com.wutsi.koki.portal.ai.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class SettingsAIControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/ai")
        assertCurrentPageIs(PageName.AI_SETTINGS)
    }

    @Test
    fun `show - without permission ai-admin`() {
        setUpUserWithoutPermissions(listOf("ai:admin"))
        navigateTo("/settings/ai")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/ai")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun configure() {
        navigateTo("/settings/ai")
        assertCurrentPageIs(PageName.AI_SETTINGS)

        click(".btn-edit")
        assertCurrentPageIs(PageName.AI_SETTINGS_EDIT)
    }
}
