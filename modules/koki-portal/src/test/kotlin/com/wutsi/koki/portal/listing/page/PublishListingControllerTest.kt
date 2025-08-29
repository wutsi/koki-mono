package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class PublishListingControllerTest : AbstractPageControllerTest() {
    @Test
    fun publish() {
        navigateTo("/listings/publish?id=${listing.id}")

        assertCurrentPageIs(PageName.LISTING_PUBLISH)
        assertElementNotPresent(".alert-danger")
        scrollToBottom()
        assertElementHasAttribute("#btn-publish", "disabled")
        click("#chk-confirm")
        click("#btn-publish")

        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/publish"),
            any(),
            eq(Any::class.java),
        )

        // DONE
        assertCurrentPageIs(PageName.LISTING_PUBLISH_DONE)
        click("#btn-continue")

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun error() {
        doThrow(createHttpClientErrorException(409, ErrorCode.LISTING_INVALID_STATUS))
            .whenever(rest)
            .postForEntity(
                eq("$sdkBaseUrl/v1/listings/${listing.id}/publish"),
                any(),
                eq(Any::class.java),
            )

        navigateTo("/listings/publish?id=${listing.id}")

        assertCurrentPageIs(PageName.LISTING_PUBLISH)
        scrollToBottom()
        assertElementHasAttribute("#btn-publish", "disabled")
        click("#chk-confirm")
        click("#btn-publish")

        assertCurrentPageIs(PageName.LISTING_PUBLISH)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `without manage AND full_access permission`() {
        setupUserWithoutPermissions(listOf("listing:manage", "listing:full_access"))

        navigateTo("/listings/publish?id=${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `publish another agent listing`() {
        setupUserWithoutPermissions(listOf("listing:full_access"))
        doReturn(
            ResponseEntity(
                GetListingResponse(listing.copy(sellerAgentUserId = 9999)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/publish?id=${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
