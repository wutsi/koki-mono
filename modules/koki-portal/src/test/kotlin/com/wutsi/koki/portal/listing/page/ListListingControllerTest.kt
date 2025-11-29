package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listings
import com.wutsi.koki.listing.dto.ListingSummary
import com.wutsi.koki.listing.dto.SearchListingResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListListingControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/listings")
        assertCurrentPageIs(PageName.LISTING_LIST)

        assertElementCount(".listing", listings.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<ListingSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(listings[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchListingResponse(20, entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchListingResponse::class.java)
            )

        navigateTo("/listings")

        assertCurrentPageIs(PageName.LISTING_LIST)
        assertElementCount(".listing", entries.size)

        scrollToBottom()
        click("#listing-load-more button")
        assertElementCount(".listing", 2 * entries.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/listings")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `list - without permission listing`() {
        setupUserWithoutPermissions(listOf("listing", "listing:full_access"))

        navigateTo("/listings")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `list - with full_access permission`() {
        setupUserWithFullAccessPermissions("listing")

        navigateTo("/listings")
        assertCurrentPageIs(PageName.LISTING_LIST)
    }
}
