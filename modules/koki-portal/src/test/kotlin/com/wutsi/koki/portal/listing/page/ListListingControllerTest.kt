package com.wutsi.koki.portal.listing.page

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
}
