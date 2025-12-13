package com.wutsi.koki.portal.lead.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.lead.dto.LeadSummary
import com.wutsi.koki.lead.dto.SearchLeadResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.LeadFixtures.leads
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListLeadControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/leads")

        assertCurrentPageIs(PageName.LEAD_LIST)
        assertElementCount("tr.lead", leads.size)
    }

    @Test
    fun loadMore() {
        val entries = mutableListOf<LeadSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(leads[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchLeadResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchLeadResponse::class.java)
            )

        navigateTo("/leads")

        assertCurrentPageIs(PageName.LEAD_LIST)
        assertElementCount("tr.lead", entries.size)

        scrollToBottom()
        click("#lead-load-more button")
        assertElementCount("tr.lead", 2 * entries.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/leads")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `list - without permission lead`() {
        setupUserWithoutPermissions(listOf("lead", "lead:full_access"))

        navigateTo("/leads")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `list - with full_access permission`() {
        setupUserWithFullAccessPermissions("lead")

        navigateTo("/leads")
        assertCurrentPageIs(PageName.LEAD_LIST)
    }
}
