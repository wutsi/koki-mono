package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.ListingFixtures
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class SearchListingControllerTest : AbstractPageControllerTest() {
    @Test
    fun search() {
        navigateTo("/listings")

        click("#btn-search")
        assertElementVisible("#koki-modal")

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

        assertCurrentPageIs(PageName.LISTING_SEARCH)
        assertElementCount(".listing", ListingFixtures.listings.size)
        assertElementCount(".listing-filter", 6)

        click(".listing-filter:first-child")
        assertElementVisible("#koki-modal")
    }
}
