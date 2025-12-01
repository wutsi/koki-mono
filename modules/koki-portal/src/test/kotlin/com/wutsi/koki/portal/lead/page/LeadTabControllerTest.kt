package com.wutsi.koki.portal.lead.page

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.LeadFixtures.leads
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class LeadTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/leads/tab?owner-id=111&owner-type=LISTING&test-mode=true")

        assertElementCount(".tab-leads tr.lead", leads.size)
    }

    @Test
    fun `list - without permission lead`() {
        setupUserWithoutPermissions(listOf("lead"))

        navigateTo("/leads/tab?owner-id=111&owner-type=LISTING&test-mode=true")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
