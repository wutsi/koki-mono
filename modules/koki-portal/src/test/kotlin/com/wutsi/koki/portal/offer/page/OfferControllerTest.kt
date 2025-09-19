package com.wutsi.koki.portal.offer.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.OfferFixtures.offer
import com.wutsi.koki.offer.dto.GetOfferResponse
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class OfferControllerTest : AbstractPageControllerTest() {
    @Test
    fun `submitted - from non-assignee`() {
        setUpOffer(OfferStatus.SUBMITTED, assigneeUserId = 333)

        navigateTo("/offers/${offer.id}")
        assertCurrentPageIs(PageName.OFFER)

        assertElementNotPresent("#btn-counter")
        assertElementNotPresent("#btn-accept")
        assertElementNotPresent("#btn-reject")
        assertElementNotPresent("#btn-cancel")
        assertElementNotPresent("#btn-close")
    }

    @Test
    fun `submitted - from assignee`() {
        setUpOffer(OfferStatus.SUBMITTED, assigneeUserId = USER_ID)

        navigateTo("/offers/${offer.id}")
        assertCurrentPageIs(PageName.OFFER)

        assertElementPresent("#btn-counter")
        assertElementPresent("#btn-accept")
        assertElementPresent("#btn-reject")
        assertElementNotPresent("#btn-cancel")
        assertElementNotPresent("#btn-close")

        assertElementsAttributeSame("#btn-accept", "#btn-accept-sticky", "href")
        assertElementsAttributeSame("#btn-reject", "#btn-reject-sticky", "href")
        assertElementsAttributeSame("#btn-counter", "#btn-counter-sticky", "href")
    }

    @Test
    fun `accepted - from buyer`() {
        setUpOffer(OfferStatus.ACCEPTED, sellerAgentUserId = 555L)

        navigateTo("/offers/${offer.id}")
        assertCurrentPageIs(PageName.OFFER)

        assertElementNotPresent("#btn-counter")
        assertElementNotPresent("#btn-accept")
        assertElementNotPresent("#btn-reject")
        assertElementNotPresent("#btn-cancel")
        assertElementNotPresent("#btn-close")
    }

    @Test
    fun `accepted - from seller`() {
        setUpOffer(OfferStatus.ACCEPTED, sellerAgentUserId = USER_ID)

        navigateTo("/offers/${offer.id}")
        assertCurrentPageIs(PageName.OFFER)

        assertElementNotPresent("#btn-counter")
        assertElementNotPresent("#btn-accept")
        assertElementNotPresent("#btn-reject")
        assertElementPresent("#btn-cancel")
        assertElementPresent("#btn-close")

        assertElementsAttributeSame("#btn-cancel", "#btn-cancel-sticky", "href")
        assertElementsAttributeSame("#btn-close", "#btn-close-sticky", "href")
    }

    private fun setUpOffer(
        status: OfferStatus,
        assigneeUserId: Long? = null,
        sellerAgentUserId: Long = USER_ID,
    ) {
        doReturn(
            ResponseEntity(
                GetOfferResponse(
                    offer.copy(
                        sellerAgentUserId = sellerAgentUserId,
                        status = status,
                        version = offer.version.copy(assigneeUserId = assigneeUserId, status = status),
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
