package com.wutsi.koki.portal.ai.page.settings

import com.wutsi.koki.TenantFixtures.config
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import kotlin.test.Test

class SettingsAIControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/ai")
        assertCurrentPageIs(PageName.AI_SETTINGS)
        assertElementNotPresent(".ai-none")
        assertElementPresent(".ai-" + config[ConfigurationName.AI_PROVIDER])
    }

    @Test
    fun `show - none`() {
        disableConfig(ConfigurationName.AI_PROVIDER)

        navigateTo("/settings/ai")
        assertCurrentPageIs(PageName.AI_SETTINGS)
        assertElementPresent(".ai-none")
    }

    @Test
    fun `show - without permission ai-admin`() {
        setUpUserWithoutPermissions(listOf("ai:admin"))
        navigateTo("/settings/ai")
        assertCurrentPageIs(PageName.ERROR_403)
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
