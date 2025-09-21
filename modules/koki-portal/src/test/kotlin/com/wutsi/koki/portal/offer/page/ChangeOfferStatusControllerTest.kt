package com.wutsi.koki.portal.offer.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.FileFixtures.image
import com.wutsi.koki.OfferFixtures.offer
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.offer.dto.GetOfferResponse
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.UpdateOfferStatusRequest
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.text.SimpleDateFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class ChangeOfferStatusControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        // Hero image
        doReturn(
            ResponseEntity(
                GetFileResponse(image),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetFileResponse::class.java)
            )
    }

    @Test
    fun close() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        setUpOffer(OfferStatus.ACCEPTED)

        navigateTo("/offers/${offer.id}")
        click("#btn-close")
        assertCurrentPageIs(PageName.OFFER_STATUS)

        assertElementNotPresent(".alert-danger")
        scrollToBottom()
        input("#closedAt", "2025\t11-10")
        input("#comment", "Yahoo!")
        click("#chk-confirm")

        setUpOffer(OfferStatus.CLOSED)
        click("#btn-close")
        val req = argumentCaptor<UpdateOfferStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/offers/${offer.id}/status"),
            req.capture(),
            eq(Any::class.java),
        )
        assertEquals(OfferStatus.CLOSED, req.firstValue.status)
        assertEquals("Yahoo!", req.firstValue.comment)
        assertEquals("2025-11-10", fmt.format(req.firstValue.closedAt))

        // Done
        assertCurrentPageIs(PageName.OFFER_STATUS_DONE)
        assertElementPresent("#script-confetti")
        click("#btn-continue")

        assertCurrentPageIs(PageName.OFFER)
    }

    @Test
    fun cancel() {
        setUpOffer(OfferStatus.ACCEPTED)

        navigateTo("/offers/${offer.id}")
        click("#btn-cancel")
        assertCurrentPageIs(PageName.OFFER_STATUS)

        assertElementNotPresent(".alert-danger")
        scrollToBottom()
        input("#comment", "Yahoo!")
        click("#chk-confirm")

        setUpOffer(OfferStatus.CANCELLED)
        click("#btn-cancel")
        val req = argumentCaptor<UpdateOfferStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/offers/${offer.id}/status"),
            req.capture(),
            eq(Any::class.java),
        )
        assertEquals(OfferStatus.CANCELLED, req.firstValue.status)
        assertEquals("Yahoo!", req.firstValue.comment)
        assertEquals(null, req.firstValue.closedAt)

        // Done
        assertCurrentPageIs(PageName.OFFER_STATUS_DONE)
        assertElementNotPresent("#script-confetti")
        click("#btn-continue")

        assertCurrentPageIs(PageName.OFFER)
    }

    @Test
    fun accept() {
        setUpOffer(OfferStatus.SUBMITTED, assigneeUserId = USER_ID)

        navigateTo("/offers/${offer.id}")
        click("#btn-accept")
        assertCurrentPageIs(PageName.OFFER_STATUS)

        assertElementNotPresent(".alert-danger")
        scrollToBottom()
        input("#comment", "Yahoo!")
        click("#chk-confirm")

        setUpOffer(OfferStatus.ACCEPTED)
        click("#btn-accept")
        val req = argumentCaptor<UpdateOfferStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/offers/${offer.id}/status"),
            req.capture(),
            eq(Any::class.java),
        )
        assertEquals(OfferStatus.ACCEPTED, req.firstValue.status)
        assertEquals("Yahoo!", req.firstValue.comment)
        assertEquals(null, req.firstValue.closedAt)

        // Done
        assertCurrentPageIs(PageName.OFFER_STATUS_DONE)
        assertElementNotPresent("#script-confetti")
        click("#btn-continue")

        assertCurrentPageIs(PageName.OFFER)
    }

    @Test
    fun reject() {
        setUpOffer(OfferStatus.SUBMITTED, assigneeUserId = USER_ID)

        navigateTo("/offers/${offer.id}")
        click("#btn-reject")
        assertCurrentPageIs(PageName.OFFER_STATUS)

        assertElementNotPresent(".alert-danger")
        scrollToBottom()
        input("#comment", "Yahoo!")
        click("#chk-confirm")

        setUpOffer(OfferStatus.REJECTED)
        click("#btn-reject")
        val req = argumentCaptor<UpdateOfferStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/offers/${offer.id}/status"),
            req.capture(),
            eq(Any::class.java),
        )
        assertEquals(OfferStatus.REJECTED, req.firstValue.status)
        assertEquals("Yahoo!", req.firstValue.comment)
        assertEquals(null, req.firstValue.closedAt)

        // Done
        assertCurrentPageIs(PageName.OFFER_STATUS_DONE)
        assertElementNotPresent("#script-confetti")
        click("#btn-continue")

        assertCurrentPageIs(PageName.OFFER)
    }

    @Test
    fun error() {
        doThrow(createHttpClientErrorException(409, ErrorCode.OFFER_BAD_STATUS))
            .whenever(rest)
            .postForEntity(
                eq("$sdkBaseUrl/v1/offers/${offer.id}/status"),
                any<UpdateOfferStatusRequest>(),
                eq(Any::class.java),
            )

        navigateTo("/offers/status?status=REJECTED&id=${offer.id}")
        assertElementNotPresent(".alert-danger")
        scrollToBottom()
        click("#chk-confirm")
        input("#comment", "Yo")
        click("#btn-reject")

        // Done
        assertCurrentPageIs(PageName.OFFER_STATUS)
        assertElementPresent(".alert-danger")
    }

    private fun setUpOffer(status: OfferStatus, assigneeUserId: Long? = null) {
        doReturn(
            ResponseEntity(
                GetOfferResponse(
                    offer.copy(
                        status = status,
                        version = offer.version.copy(status = status, assigneeUserId = assigneeUserId)
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
