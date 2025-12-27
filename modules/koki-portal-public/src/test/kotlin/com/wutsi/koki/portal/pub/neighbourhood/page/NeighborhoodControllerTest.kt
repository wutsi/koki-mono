package com.wutsi.koki.portal.pub.neighbourhood.page

import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.RefDataFixtures.neighborhoods
import com.wutsi.koki.portal.pub.common.page.PageName
import kotlin.test.Test

class NeighborhoodControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/neighbourhoods/${neighborhoods[0].id}")
        assertCurrentPageIs(PageName.NEIGHBOURHOOD)

        assertCurrentPageIs(PageName.NEIGHBOURHOOD)
        assertElementPresent("#map-container")
        assertElementPresent("#agent-container")
        assertElementPresent("#rental-listing-container")
        assertElementPresent("#sale-listing-container")
        assertElementPresent("#sold-listing-container")
    }
}
