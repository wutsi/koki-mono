package com.wutsi.koki.portal.pub.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.ListingSummary
import com.wutsi.koki.listing.dto.SearchListingResponse
import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.ListingFixtures
import com.wutsi.koki.portal.pub.common.page.PageName
import org.openqa.selenium.Keys
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class SearchControllerTest : AbstractPageControllerTest() {
    @Test
    fun result() {
        // WHEN
        navigateTo("/search")

        // THEN
        assertCurrentPageIs(PageName.SEARCH)

        assertElementPresent("#listing-filter-container")
        assertElementPresent("#location-id")
        assertElementPresent("#btn-filter-listing-type")
        assertElementPresent("#btn-filter-property-category")
        assertElementPresent("#btn-filter-bedroom")
        assertElementPresent("#btn-filter-price")

        assertElementPresent("#listing-sort-container")
        assertElementPresent("#btn-sort-by")

        assertElementNotPresent("#listing-no-offer-container")
        assertElementPresent("#listing-offer-container")
        assertElementCount("#listing-offer-container .listing-card", ListingFixtures.listings.size)
        assertElementNotPresent("#btn-load-more")
    }

    @Test
    fun `result - 1 result`() {
        // GIVEN
        doReturn(
            ResponseEntity(
                SearchListingResponse(
                    listings = listOf(ListingFixtures.listings[0]),
                    total = ListingFixtures.listings.size.toLong(),
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchListingResponse::class.java)
            )

        // WHEN
        navigateTo("/search")

        // THEN
        assertCurrentPageIs(PageName.SEARCH)

        assertElementPresent("#listing-filter-container")
        assertElementPresent("#location-id")
        assertElementPresent("#btn-filter-listing-type")
        assertElementPresent("#btn-filter-property-category")
        assertElementPresent("#btn-filter-bedroom")
        assertElementPresent("#btn-filter-price")

        assertElementPresent("#listing-sort-container")
        assertElementPresent("#btn-sort-by")

        assertElementNotPresent("#listing-no-offer-container")
        assertElementPresent("#listing-offer-container")
        assertElementCount("#listing-offer-container .listing-card", 1)
        assertElementNotPresent("#btn-load-more")
    }

    @Test
    fun `result - no result`() {
        // GIVEN
        doReturn(
            ResponseEntity(
                SearchListingResponse(
                    listings = listOf(),
                    total = 0,
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchListingResponse::class.java)
            )

        // WHEN
        navigateTo("/search")

        // THEN
        assertCurrentPageIs(PageName.SEARCH)

        assertElementPresent("#listing-filter-container")
        assertElementPresent("#location-id")
        assertElementPresent("#btn-filter-listing-type")
        assertElementPresent("#btn-filter-property-category")
        assertElementPresent("#btn-filter-bedroom")
        assertElementNotPresent("#btn-filter-price")

        assertElementNotPresent("#listing-sort-container")
        assertElementNotPresent("#btn-sort-by")

        assertElementPresent("#listing-no-offer-container")
        assertElementNotPresent("#listing-offer-container")
        assertElementNotPresent("#btn-load-more")
    }

    @Test
    fun `filter by listing-type`() {
        // GIVEN

        // WHEN
        navigateTo("/search")
        verify(rest, atLeast(1)).getForEntity(
            any<String>(),
            eq(SearchListingResponse::class.java)
        )

        click("#btn-filter-listing-type")
        click("[data-value=RENTAL]")

        // THEN
        verify(rest, atLeast(1)).getForEntity(
            any<String>(),
            eq(SearchListingResponse::class.java)
        )

        assertCurrentPageIs(PageName.SEARCH)
    }

    @Test
    fun `filter by property-category`() {
        // GIVEN

        // WHEN
        navigateTo("/search")
        verify(rest, atLeast(1)).getForEntity(
            any<String>(),
            eq(SearchListingResponse::class.java)
        )

        click("#btn-filter-property-category")
        click("[data-value=LAND]")

        // THEN
        verify(rest, atLeast(1)).getForEntity(
            any<String>(),
            eq(SearchListingResponse::class.java)
        )

        assertCurrentPageIs(PageName.SEARCH)
    }

    @Test
    fun `filter by bedrooms`() {
        // GIVEN

        // WHEN
        navigateTo("/search")
        verify(rest, atLeast(1)).getForEntity(
            any<String>(),
            eq(SearchListingResponse::class.java)
        )

        click("#btn-filter-bedroom")
        click("[data-value='3+']")

        // THEN
        verify(rest, atLeast(1)).getForEntity(
            any<String>(),
            eq(SearchListingResponse::class.java)
        )

        assertCurrentPageIs(PageName.SEARCH)
    }

    @Test
    fun `filter by price`() {
        // GIVEN

        // WHEN
        navigateTo("/search")
        verify(rest, atLeast(1)).getForEntity(
            any<String>(),
            eq(SearchListingResponse::class.java)
        )

        click("#btn-filter-price")
        input("#price-range", Keys.LEFT.name)
        input("#price-range", Keys.LEFT.name)
        click("#btn-apply-price-filter")

        // THEN
        verify(rest, atLeast(1)).getForEntity(
            any<String>(),
            eq(SearchListingResponse::class.java)
        )

        assertCurrentPageIs(PageName.SEARCH)
    }

    @Test
    fun `load more`() {
        // GIVEN
        val entries = mutableListOf<ListingSummary>()
        var seed = System.currentTimeMillis()
        repeat(SearchController.LIMIT) {
            entries.add(ListingFixtures.listings[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchListingResponse(
                    listings = entries,
                    total = entries.size.toLong() + ListingFixtures.listings.size,
                ),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchListingResponse(
                    listings = ListingFixtures.listings,
                    total = ListingFixtures.listings.size.toLong() + ListingFixtures.listings.size,
                ),
                HttpStatus.OK,
            )

        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchListingResponse::class.java)
            )

        // WHEN
        navigateTo("/search")
        scrollToBottom()
        click("#btn-load-more")

        // THEN
        assertCurrentPageIs(PageName.SEARCH)

        assertElementNotPresent("#btn-load-more")
        assertElementCount("#listing-offer-container .listing-card", entries.size + ListingFixtures.listings.size)
    }
}
