package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listings
import com.wutsi.koki.listing.dto.SearchListingResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListingWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun recent() {
        navigateTo("/listings/widgets/recent?test-mode=true")
        assertElementCount(".widget .listing-card", listings.size)
    }

    @Test
    fun sold() {
        navigateTo("/listings/widgets/sold?test-mode=true")
        assertElementCount(".widget .listing-card", listings.size)
    }

    @Test
    fun empty() {
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

        navigateTo("/listings/widgets/sold?test-mode=true")
        assertElementNotPresent(".widget")
    }
}
