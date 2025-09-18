package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.OfferFixtures.offers
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class ListOfferControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/offers")
        assertCurrentPageIs(PageName.OFFER_LIST)
        assertElementCount(".offer", offers.size)
    }

    @Test
    fun `without permission offer`() {
        setupUserWithoutPermissions(listOf("offer:full_access", "offer"))

        navigateTo("/offers")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
