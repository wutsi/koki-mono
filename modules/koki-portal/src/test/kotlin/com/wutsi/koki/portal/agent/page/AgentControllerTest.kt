package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.AgentFixtures.agents
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class ListAgentControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/agents")

        assertCurrentPageIs(PageName.AGENT_LIST)
        assertElementCount("div.agent", agents.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/agents")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `list - without permission agent`() {
        setupUserWithoutPermissions(listOf("agent"))

        navigateTo("/agents")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
