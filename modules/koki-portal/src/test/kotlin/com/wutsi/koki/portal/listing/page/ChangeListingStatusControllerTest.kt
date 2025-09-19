package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.dto.CloseListingRequest
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class ChangeListingStatusControllerTest : AbstractPageControllerTest() {
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

        assertCurrentPageIs(PageName.LISTING_STATUS_CLOSE)
        assertElementPresent(".alert-danger")
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
