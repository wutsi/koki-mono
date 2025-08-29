package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListingControllerTest : AbstractPageControllerTest() {
    @Test
    fun sale() {
        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)
        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementNotPresent("#listing-leasing-section")
        assertElementPresent("#seller-agent-commission")
        assertElementPresent("#listing-seller-section")
    }

    @Test
    fun rental() {
        doReturn(
            ResponseEntity(
                GetListingResponse(listing.copy(listingType = ListingType.RENTAL)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)
        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementPresent("#listing-leasing-section")
        assertElementPresent("#seller-agent-commission")
        assertElementPresent("#listing-seller-section")
    }

    @Test
    fun `view another agent listing ACTIVE`() {
        doReturn(
            ResponseEntity(
                GetListingResponse(listing.copy(sellerAgentUserId = 9999, status = ListingStatus.ACTIVE)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)
        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementNotPresent("#seller-agent-commission")
        assertElementNotPresent("#listing-seller-section")
    }

    @Test
    fun `view another agent listing DRAFT`() {
        doReturn(
            ResponseEntity(
                GetListingResponse(listing.copy(sellerAgentUserId = 9999, status = ListingStatus.DRAFT)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `without listing AND full_access`() {
        setupUserWithoutPermissions(listOf("listing", "listing:full_access"))

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
