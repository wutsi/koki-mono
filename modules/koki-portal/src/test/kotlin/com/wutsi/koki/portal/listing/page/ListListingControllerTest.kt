package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.ListingFixtures.listings
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class ListListingControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/listings")
        assertCurrentPageIs(PageName.LISTING_LIST)

        assertElementCount(".listing", listings.size)
    }
}
