package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class ListingDebugControllerTest : AbstractPageControllerTest() {
    @Test
    fun debug() {
        navigateTo("/listings/debug?id=${listing.id}")
        assertCurrentPageIs(PageName.LISTING_DEBUG)

        assertElementPresent("#webpage-container")
    }
}
