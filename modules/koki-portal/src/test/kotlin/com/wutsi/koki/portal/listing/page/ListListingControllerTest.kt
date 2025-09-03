package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.ListingFixtures
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class ListListingControllerTest : AbstractPageControllerTest() {
    @Test
    fun `my listings`() {
        navigateTo("/listings")
        assertCurrentPageIs(PageName.LISTING_LIST)

        assertElementPresent("#for-sale-container")
        assertElementPresent("#for-rent-container")
        assertElementPresent("#sold-container")
    }

    @Test
    fun filter() {
        navigateTo("/listings")
        assertCurrentPageIs(PageName.LISTING_LIST)

        click("#btn-search")
        click("#lbl-listing-type-RENTAL")
        click("#lbl-property-type-HOUSE")
        click("#lbl-property-type-APARTMENT")
        click("#lbl-bedrooms-2")
        click("#lbl-bathrooms-3")
        input("#minPrice", "500")
        input("#maxPrice", "2500")
        input("#minLotArea", "1000")
        input("#maxLotArea", "2000")
        input("#minPropertyArea", "700")
        input("#maxPropertyArea", "1300")
        click("button[type=submit]")

        assertElementCount(".listing-card", ListingFixtures.listings.size)
//
//        val url = argumentCaptor<String>()
//        verify(rest).getForEntity(
//            url.capture(),
//            eq(SearchListingResponse::class.java)
//        )
//        println(url.firstValue)
    }
}
