package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.OfferFixtures.offer
import com.wutsi.koki.OfferFixtures.offerVersions
import com.wutsi.koki.portal.AbstractPageControllerTest
import kotlin.test.Test

class ListOfferVersionControllerTest : AbstractPageControllerTest() {
    @Test
    fun widget() {
        navigateTo("/offer-versions?test-mode=true&offer-id=${offer.id}")
        assertElementCount(".version", offerVersions.size)

        click(".version td a")
        assertElementVisible("#koki-modal")
    }
}
