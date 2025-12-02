package com.wutsi.koki.portal.lead.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.lead.dto.GetLeadResponse
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.UpdateLeadStatusRequest
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.LeadFixtures.lead
import com.wutsi.koki.portal.common.page.PageName
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.assertNotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class ChangeLeadStatusControllerTest : AbstractPageControllerTest() {
    private val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")

    @Test
    fun edit() {
        navigateTo("/leads/status?id=${lead.id}")

        assertCurrentPageIs(PageName.LEAD_STATUS)
        click("#chk-status-CONTACTED")
        click("button[type=submit]")

        val request = argumentCaptor<UpdateLeadStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/leads/${lead.id}/status"),
            request.capture(),
            eq(Any::class.java),
        )
        assertEquals(LeadStatus.CONTACTED, request.firstValue.status)
        assertEquals(fmt.format(lead.nextVisitAt), fmt.format(request.firstValue.nextVisitAt))
        assertEquals(fmt.format(lead.nextContactAt), fmt.format(request.firstValue.nextContactAt))

        assertCurrentPageIs(PageName.LEAD_STATUS_DONE)
        click("#btn-continue")
        assertCurrentPageIs(PageName.LEAD)
    }

    @Test
    fun contactLater() {
        setupLead(LeadStatus.CONTACT_LATER)

        navigateTo("/leads/status?id=${lead.id}")

        val date = fmt.format(DateUtils.addDays(Date(), 3)).replaceFirst("-", "\t")
        assertCurrentPageIs(PageName.LEAD_STATUS)
        click("#chk-status-" + LeadStatus.CONTACT_LATER)
        scrollToBottom()
        input("#nextContactAt", date)
        click("button[type=submit]")

        val request = argumentCaptor<UpdateLeadStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/leads/${lead.id}/status"),
            request.capture(),
            eq(Any::class.java),
        )
        assertEquals(LeadStatus.CONTACT_LATER, request.firstValue.status)
        assertEquals(fmt.format(lead.nextVisitAt), fmt.format(request.firstValue.nextVisitAt))
        assertNotNull(request.firstValue.nextContactAt)

        assertCurrentPageIs(PageName.LEAD_STATUS_DONE)
        click("#btn-continue")
        assertCurrentPageIs(PageName.LEAD)
    }

    @Test
    fun visitSet() {
        setupLead(LeadStatus.VISIT_SET)

        navigateTo("/leads/status?id=${lead.id}")

        val date = fmt.format(DateUtils.addDays(Date(), 3)).replaceFirst("-", "\t")
        assertCurrentPageIs(PageName.LEAD_STATUS)
        click("#chk-status-" + LeadStatus.VISIT_SET)
        scrollToBottom()
        input("#nextVisitAt", date)
        click("button[type=submit]")

        val request = argumentCaptor<UpdateLeadStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/leads/${lead.id}/status"),
            request.capture(),
            eq(Any::class.java),
        )
        assertEquals(LeadStatus.VISIT_SET, request.firstValue.status)
        assertEquals(fmt.format(lead.nextContactAt), fmt.format(request.firstValue.nextContactAt))
        assertNotNull(request.firstValue.nextVisitAt)

        assertCurrentPageIs(PageName.LEAD_STATUS_DONE)
        click("#btn-continue")
        assertCurrentPageIs(PageName.LEAD)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/leads/status?id=${lead.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `show - without permission lead`() {
        setupUserWithoutPermissions(listOf("lead", "lead:full_access"))

        navigateTo("/leads/status?id=${lead.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `show - with full_access permission`() {
        setupUserWithFullAccessPermissions("lead")

        navigateTo("/leads/status?id=${lead.id}")
        assertCurrentPageIs(PageName.LEAD_STATUS)
    }

    @Test
    fun `show - agent cannot access another agent lead`() {
        setupListing(sellerAgentUserId = 7777)

        navigateTo("/leads/status?id=${lead.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `show - full_access agent can access another agent lead`() {
        setupUserWithFullAccessPermissions("lead")

        setupListing(sellerAgentUserId = 7777)

        navigateTo("/leads/status?id=${lead.id}")
        assertCurrentPageIs(PageName.LEAD_STATUS)
    }

    private fun setupListing(sellerAgentUserId: Long? = null) {
        doReturn(
            ResponseEntity(
                GetListingResponse(
                    listing.copy(
                        id = lead.listingId ?: -1,
                        sellerAgentUserId = sellerAgentUserId ?: listing.sellerAgentUserId,
                    )
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java),
            )
    }

    private fun setupLead(status: LeadStatus) {
        doReturn(
            ResponseEntity(
                GetLeadResponse(
                    lead.copy(status = status)
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetLeadResponse::class.java),
            )
    }
}
