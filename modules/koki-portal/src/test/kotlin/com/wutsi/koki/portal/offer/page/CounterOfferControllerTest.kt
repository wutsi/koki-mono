package com.wutsi.koki.portal.offer.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.OfferFixtures.offer
import com.wutsi.koki.TenantFixtures
import com.wutsi.koki.offer.dto.CreateOfferVersionRequest
import com.wutsi.koki.offer.dto.CreateOfferVersionResponse
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class CounterOfferControllerTest : AbstractPageControllerTest() {
    @Test
    fun counter() {
        navigateTo("/offers/counter?id=${offer.id}")
        assertCurrentPageIs(PageName.OFFER_COUNTER)

        // Create
        input("#price", "300000")
        scrollToBottom()
        input("#contingencies", "I love it!")
        click("button[type=submit]")
        val req = argumentCaptor<CreateOfferVersionRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/offer-versions"),
            req.capture(),
            eq(CreateOfferVersionResponse::class.java),
        )
        assertEquals(offer.id, req.firstValue.offerId)
        assertEquals(300000L, req.firstValue.price)
        assertEquals(TenantFixtures.tenants[0].currency, req.firstValue.currency)
        assertEquals("I love it!", req.firstValue.contingencies)
        assertEquals(OfferParty.SELLER, req.firstValue.submittingParty)

        // Done
        assertCurrentPageIs(PageName.OFFER_COUNTER_DONE)
        click("#btn-continue")

        assertCurrentPageIs(PageName.OFFER)
    }
}
