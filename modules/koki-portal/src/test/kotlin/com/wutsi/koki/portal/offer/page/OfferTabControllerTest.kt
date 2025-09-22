package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.OfferFixtures.offers
import com.wutsi.koki.portal.AbstractPageControllerTest
import kotlin.test.Test

class OfferTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun tab() {
        navigateTo("/offers/tab?offer-id=${listing.id}&offer-type=LISTING&test-mode=true")

        assertElementCount(".tab-offers .offer", offers.size)
    }

    @Test
    fun readOnly() {
        navigateTo("/offers/tab?offer-id=${listing.id}&offer-type=LISTING&test-mode=true&read-only=true")

        assertElementNotPresent("#btn-create-offer")
        assertElementCount(".tab-offers .offer", offers.size)
    }

    @Test
    fun `without permission manage`() {
        setupUserWithoutPermissions(listOf("offer:full_access", "offer:manage"))

        navigateTo("/offers/tab?offer-id=${listing.id}&offer-type=LISTING&test-mode=true")

        assertElementNotPresent("#btn-create-offer")
        assertElementCount(".tab-offers .offer", offers.size)
    }
}
