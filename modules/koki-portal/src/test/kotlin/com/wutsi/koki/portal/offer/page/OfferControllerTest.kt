package com.wutsi.koki.portal.offer.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.OfferFixtures.offer
import com.wutsi.koki.offer.dto.GetOfferResponse
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class OfferControllerTest : AbstractPageControllerTest() {
    @Test
    fun `submitted BUYER`() {
        setUpOffer(OfferStatus.SUBMITTED, USER_ID, 555L, OfferParty.BUYER)

        navigateTo("/offers/${offer.id}")
        assertCurrentPageIs(PageName.OFFER)

        assertElementPresent("#btn-counter")
        assertElementPresent("#btn-accept")
        assertElementPresent("#btn-refuse")
        assertElementNotPresent("#btn-withdraw")
    }

    @Test
    fun `submitted SELLER`() {
        setUpOffer(OfferStatus.SUBMITTED, 555L, USER_ID, OfferParty.SELLER)

        navigateTo("/offers/${offer.id}")
        assertCurrentPageIs(PageName.OFFER)

        assertElementPresent("#btn-counter")
        assertElementPresent("#btn-accept")
        assertElementPresent("#btn-refuse")
        assertElementNotPresent("#btn-withdraw")
    }

    @Test
    fun submitted() {
        setUpOffer(OfferStatus.SUBMITTED, USER_ID, 555L, OfferParty.SELLER)

        navigateTo("/offers/${offer.id}")
        assertCurrentPageIs(PageName.OFFER)

        assertElementNotPresent("#btn-counter")
        assertElementNotPresent("#btn-accept")
        assertElementNotPresent("#btn-refuse")
        assertElementPresent("#btn-withdraw")
    }

    @Test
    fun accepted() {
        setUpOffer(OfferStatus.ACCEPTED, USER_ID, 555L, OfferParty.SELLER)

        navigateTo("/offers/${offer.id}")
        assertCurrentPageIs(PageName.OFFER)

        assertElementNotPresent("#btn-counter")
        assertElementNotPresent("#btn-accept")
        assertElementNotPresent("#btn-refuse")
        assertElementNotPresent("#btn-withdraw")
    }

    private fun setUpOffer(
        status: OfferStatus,
        sellerAgentUserId: Long,
        buyerAgentUserId: Long,
        submittingParty: OfferParty,
    ) {
        doReturn(
            ResponseEntity(
                GetOfferResponse(
                    offer.copy(
                        sellerAgentUserId = sellerAgentUserId,
                        buyerAgentUserId = buyerAgentUserId,
                        status = status,
                        version = offer.version.copy(submittingParty = submittingParty, status = status),
                    )
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetOfferResponse::class.java)
            )
    }
}
