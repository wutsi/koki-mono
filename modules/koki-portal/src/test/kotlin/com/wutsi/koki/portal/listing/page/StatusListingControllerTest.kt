package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.dto.CloseListingRequest
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.text.SimpleDateFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusListingControllerTest : AbstractPageControllerTest() {
    @Test
    fun cancel() {
        navigateTo("/listings/status?id=${listing.id}")
        assertCurrentPageIs(PageName.LISTING_STATUS)
        assertElementHasAttribute("#btn-next", "disabled")
        click("#chk-status-CANCELLED")
        click("#btn-next")

        setupListing(ListingStatus.CANCELLED)
        assertCurrentPageIs(PageName.LISTING_STATUS_CLOSE)
        assertElementNotPresent(".alert-danger")
        assertElementHasAttribute("#btn-next", "disabled")
        input("#comment", "Im done with this shit")
        click("#chk-confirm")
        click("#btn-next")
        val req = argumentCaptor<CloseListingRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/close"),
            req.capture(),
            eq(Any::class.java),
        )
        assertEquals(ListingStatus.CANCELLED, req.firstValue.status)
        assertEquals(null, req.firstValue.buyerEmail)
        assertEquals(null, req.firstValue.buyerPhone)
        assertEquals(null, req.firstValue.buyerAgentUserId)
        assertEquals(null, req.firstValue.transactionPrice)
        assertEquals(null, req.firstValue.transactionPrice)
        assertEquals(null, req.firstValue.transactionDate)
        assertEquals("Im done with this shit", req.firstValue.comment)

        assertCurrentPageIs(PageName.LISTING_STATUS_DONE)
        assertElementNotPresent("#script-confetti")
        click("#btn-continue")
        assertCurrentPageIs(PageName.LISTING_LIST)
    }

    @Test
    fun error() {
        doThrow(createHttpClientErrorException(409, ErrorCode.LISTING_INVALID_STATUS))
            .whenever(rest)
            .postForEntity(
                eq("$sdkBaseUrl/v1/listings/${listing.id}/close"),
                any(),
                eq(Any::class.java),
            )

        navigateTo("/listings/status?id=${listing.id}")
        assertCurrentPageIs(PageName.LISTING_STATUS)
        assertElementHasAttribute("#btn-next", "disabled")
        click("#chk-status-CANCELLED")
        click("#btn-next")

        setupListing(ListingStatus.WITHDRAWN)
        assertCurrentPageIs(PageName.LISTING_STATUS_CLOSE)
        assertElementHasAttribute("#btn-next", "disabled")
        input("#comment", "Im done with this shit")
        click("#chk-confirm")
        click("#btn-next")

        assertCurrentPageIs(PageName.LISTING_STATUS_DONE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun sale() {
        navigateTo("/listings/status?id=${listing.id}")
        assertCurrentPageIs(PageName.LISTING_STATUS)
        assertElementHasAttribute("#btn-next", "disabled")
        click("#chk-status-SOLD")
        click("#btn-next")

        setupListing(ListingStatus.SOLD)
        assertCurrentPageIs(PageName.LISTING_STATUS_CLOSE)
        assertElementNotPresent(".alert-danger")
        assertElementHasAttribute("#btn-next", "disabled")
        input("#buyerName", "Ray Sponsible")
        input("#buyerEmail", "Ray.Sponsible@gmail.com")
        input("#buyerPhone", "5147580011")
        input("#transactionPrice", "150000")
        scrollToBottom()
        input("#transactionDate", "2020\t0301")
        select2("#buyerAgentUserId", users[0].displayName)
        input("#comment", "My first TX")
        click("#chk-confirm")
        click("#btn-next")
        val req = argumentCaptor<CloseListingRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/close"),
            req.capture(),
            eq(Any::class.java),
        )
        assertEquals(ListingStatus.SOLD, req.firstValue.status)
        assertEquals("Ray Sponsible", req.firstValue.buyerName)
        assertEquals("Ray.Sponsible@gmail.com", req.firstValue.buyerEmail)
        assertEquals("+15147580011", req.firstValue.buyerPhone)
        assertEquals(users[0].id, req.firstValue.buyerAgentUserId)
        assertEquals(150000L, req.firstValue.transactionPrice)
        assertEquals("2020-03-01", SimpleDateFormat("yyyy-MM-dd").format(req.firstValue.transactionDate))
        assertEquals("My first TX", req.firstValue.comment)

        assertCurrentPageIs(PageName.LISTING_STATUS_DONE)
        assertElementPresent("#script-confetti")
        click("#btn-continue")
        assertCurrentPageIs(PageName.LISTING_LIST)
    }

    private fun setupListing(
        status: ListingStatus = ListingStatus.ACTIVE,
        sellerAgentUserId: Long = USER_ID,
        listingType: ListingType = ListingType.RENTAL,
    ) {
        doReturn(
            ResponseEntity(
                GetListingResponse(
                    listing.copy(
                        status = status,
                        sellerAgentUserId = sellerAgentUserId,
                        listingType = listingType,
                    )
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )
    }
}
