package com.wutsi.koki.portal.offer.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.ContactFixtures.contact
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.TenantFixtures
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.CreateOfferResponse
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateOfferControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/offers/create?listing-id=${listing.id}")
        assertCurrentPageIs(PageName.OFFER_CREATE)

        // Create
        select2("#buyerContactId", "${contact.firstName} ${contact.lastName}")
        input("#price", "300000")
        scrollToBottom()
        input("#contingencies", "I love it!")
        click("button[type=submit]")
        val req = argumentCaptor<CreateOfferRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/offers"),
            req.capture(),
            eq(CreateOfferResponse::class.java),
        )
        assertEquals(listing.sellerAgentUserId, req.firstValue.sellerAgentUserId)
        assertEquals(USER_ID, req.firstValue.buyerAgentUserId)
        assertEquals(contact.id, req.firstValue.buyerContactId)
        assertEquals(300000L, req.firstValue.price)
        assertEquals(TenantFixtures.tenants[0].currency, req.firstValue.currency)
        assertEquals("I love it!", req.firstValue.contingencies)
        assertEquals(OfferParty.BUYER, req.firstValue.submittingParty)
        assertEquals(listing.id, req.firstValue.owner?.id)
        assertEquals(ObjectType.LISTING, req.firstValue.owner?.type)

        // Done
        assertCurrentPageIs(PageName.OFFER_CREATE_DONE)
        click("#btn-continue")

        assertCurrentPageIs(PageName.OFFER)
    }
}
