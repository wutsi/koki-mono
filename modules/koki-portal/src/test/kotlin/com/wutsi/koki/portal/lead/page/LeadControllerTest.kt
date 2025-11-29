package com.wutsi.koki.portal.lead.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.LeadFixtures.lead
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class LeadControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/leads/${lead.id}")

        assertCurrentPageIs(PageName.LEAD)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/leads/${lead.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `show - without permission lead`() {
        setupUserWithoutPermissions(listOf("lead", "lead:full_access"))

        navigateTo("/leads/${lead.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `show - with full_access permission`() {
        setupUserWithFullAccessPermissions("lead")

        navigateTo("/leads/${lead.id}")
        assertCurrentPageIs(PageName.LEAD)
    }

    @Test
    fun `show - agent cannot access another agent lead`() {
        setupListing(sellerAgentUserId = 7777)

        navigateTo("/leads/${lead.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `show - full_access agent can access another agent lead`() {
        setupUserWithFullAccessPermissions("lead")

        setupListing(sellerAgentUserId = 7777)

        navigateTo("/leads/${lead.id}")
        assertCurrentPageIs(PageName.LEAD)
    }

    private fun setupListing(
        sellerAgentUserId: Long? = null,
    ) {
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
}
