package com.wutsi.koki.portal.translation.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class SettingsTranslationControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/translations")
        assertCurrentPageIs(PageName.TRANSLATION_SETTINGS)
    }

    @Test
    fun `show - without permission translation-admin`() {
        setUpUserWithoutPermissions(listOf("translation:admin"))
        navigateTo("/settings/translations")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/translations")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun configure() {
        navigateTo("/settings/translations")
        assertCurrentPageIs(PageName.TRANSLATION_SETTINGS)

        click(".btn-edit")
        assertCurrentPageIs(PageName.TRANSLATION_SETTINGS_EDIT)
    }
}
