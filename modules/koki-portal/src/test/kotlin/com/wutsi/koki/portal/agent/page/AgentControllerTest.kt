package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.AgentFixtures.agent
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class AgentControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/agents/${agent.id}")
        assertCurrentPageIs(PageName.AGENT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()
        navigateTo("/agents/${agent.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
