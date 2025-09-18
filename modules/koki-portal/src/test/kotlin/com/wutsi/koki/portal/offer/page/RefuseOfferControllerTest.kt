package com.wutsi.koki.portal.offer.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.OfferFixtures.offer
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.offer.dto.CreateOfferResponse
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.UpdateOfferStatusRequest
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class AcceptOfferControllerTest : AbstractPageControllerTest() {
    @Test
    fun accept() {
        navigateTo("/offers/accept?id=${offer.id}")
        assertCurrentPageIs(PageName.OFFER_ACCEPT)

        assertElementNotPresent(".alert-danger")
        scrollToBottom()
        click("#chk-confirm")
        click("#btn-accept")
        val req = argumentCaptor<UpdateOfferStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/offers/${offer.id}/status"),
            req.capture(),
            eq(Any::class.java),
        )
        assertEquals(OfferStatus.ACCEPTED, req.firstValue.status)
        assertEquals(null, req.firstValue.reason)

        // Done
        assertCurrentPageIs(PageName.OFFER_ACCEPT_DONE)
        click("#btn-continue")

        assertCurrentPageIs(PageName.OFFER)
    }

    @Test
    fun error() {
        doThrow(createHttpClientErrorException(409, ErrorCode.OFFER_NOT_FOUND))
            .whenever(rest)
            .postForEntity(
                eq("$sdkBaseUrl/v1/offers/${offer.id}/status"),
                any<UpdateOfferStatusRequest>(),
                eq(Any::class.java),
            )

        navigateTo("/offers/accept?id=${offer.id}")
        assertCurrentPageIs(PageName.OFFER_ACCEPT)

        assertElementNotPresent(".alert-danger")
        scrollToBottom()
        click("#chk-confirm")
        click("#btn-accept")

        // Done
        assertCurrentPageIs(PageName.OFFER_ACCEPT)
        assertElementPresent(".alert-danger")
    }
}
