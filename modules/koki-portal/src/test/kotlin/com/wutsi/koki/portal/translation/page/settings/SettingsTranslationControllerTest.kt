package com.wutsi.koki.portal.translation.page.settings

import com.wutsi.koki.TenantFixtures.config
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import kotlin.test.Test

class SettingsTranslationControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/translations")
        assertCurrentPageIs(PageName.TRANSLATION_SETTINGS)
        assertElementPresent(".translation-" + config[ConfigurationName.TRANSLATION_PROVIDER])
        assertElementNotPresent(".translation-none")
    }

    @Test
    fun `show - none`() {
        disableConfig(ConfigurationName.TRANSLATION_PROVIDER)
        navigateTo("/settings/translations")
        assertCurrentPageIs(PageName.TRANSLATION_SETTINGS)
        assertElementPresent(".translation-none")
    }

    @Test
    fun `show - without permission translation-admin`() {
        setupUserWithoutPermissions(listOf("translation:admin"))
        navigateTo("/settings/translations")
        assertCurrentPageIs(PageName.ERROR_403)
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
