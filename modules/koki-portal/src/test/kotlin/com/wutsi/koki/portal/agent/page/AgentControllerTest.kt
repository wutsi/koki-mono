package com.wutsi.koki.portal.agent.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listings
import com.wutsi.koki.listing.dto.SearchListingResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.AgentFixtures.agent
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class AgentControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/agents/${agent.id}")
        assertCurrentPageIs(PageName.AGENT)
        assertElementCount("#active-listings .listing", listings.size)
        assertElementCount("#sold-listings .listing", listings.size)
    }

    @Test
    fun `no listings`() {
        doReturn(
            ResponseEntity(
                SearchListingResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchListingResponse::class.java)
            )

        navigateTo("/agents/${agent.id}")
        Thread.sleep(2000)
        assertCurrentPageIs(PageName.AGENT)
        assertElementNotPresent("#active-listings")
        assertElementNotPresent("#sold-listings")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()
        navigateTo("/agents/${agent.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
