package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.UserFixtures.users
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
        setupListing(listingType = ListingType.SALE)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)
        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geo-location-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementNotPresent("#listing-leasing-section")
        assertElementPresent("#seller-agent-commission")
        assertElementPresent("#listing-seller-section")
        assertElementNotPresent("#listing-sale-section")
    }

    @Test
    fun rental() {
        setupListing(listingType = ListingType.RENTAL)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)
        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geo-location-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementPresent("#listing-leasing-section")
        assertElementPresent("#seller-agent-commission")
        assertElementPresent("#listing-seller-section")
        assertElementNotPresent("#listing-sale-section")
    }

    @Test
    fun `view DRAFT listing`() {
        setupListing(status = ListingStatus.DRAFT)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementPresent("#btn-map")
        assertElementNotPresent("#btn-share")
        assertElementPresent("#btn-edit")
        assertElementPresent("#btn-publish")
        assertElementNotPresent("#btn-status")
        assertElementCount(".btn-section-edit", 8)
    }

    @Test
    fun `view ACTIVE listing`() {
        setupListing(status = ListingStatus.ACTIVE)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementPresent("#btn-map")
        assertElementNotPresent("#btn-share")
        assertElementNotPresent("#btn-edit")
        assertElementNotPresent("#btn-publish")
        assertElementPresent("#btn-status")
        assertElementCount(".btn-section-edit", 0)
    }

    @Test
    fun `view RENTED listing`() {
        setupListing(status = ListingStatus.RENTED, listingType = ListingType.RENTAL)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementPresent("#btn-map")
        assertElementNotPresent("#btn-share")
        assertElementNotPresent("#btn-edit")
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-status")

        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geo-location-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementPresent("#listing-leasing-section")
        assertElementNotPresent("#seller-agent-commission")
        assertElementNotPresent("#buyer-agent-commission")
        assertElementPresent("#listing-seller-section")
        assertElementPresent("#listing-sale-section")
        assertElementPresent("#buyer-contact-info")
        assertElementPresent("#final-seller-agent-commission")
        assertElementPresent("#final-buyer-agent-commission")
        assertElementCount(".btn-section-edit", 0)
    }

    @Test
    fun `view SOLD listing from another agent`() {
        setupListing(status = ListingStatus.SOLD, listingType = ListingType.SALE, sellerAgentUserId = users[1].id)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementPresent("#btn-map")
        assertElementNotPresent("#btn-share")
        assertElementNotPresent("#btn-edit")
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-status")

        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geo-location-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementPresent("#listing-sale-section")

        assertElementNotPresent("#seller-agent-commission")
        assertElementNotPresent("#buyer-agent-commission")
        assertElementNotPresent("#listing-seller-section")
        assertElementNotPresent("#buyer-contact-info")
        assertElementNotPresent("#final-seller-agent-commission")
        assertElementCount(".btn-section-edit", 0)
    }

    @Test
    fun `view another agent listing ACTIVE`() {
        setupListing(status = ListingStatus.ACTIVE, sellerAgentUserId = 9999L)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)
        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geo-location-section")
        assertElementPresent("#listing-price-section")
        assertElementNotPresent("#seller-agent-commission")
        assertElementNotPresent("#listing-seller-section")

        assertElementPresent("#btn-map")
        assertElementNotPresent("#btn-share")
        assertElementNotPresent("#btn-edit")
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-status")
        assertElementCount(".btn-section-edit", 0)
    }

    @Test
    fun `view another agent listing DRAFT`() {
        setupListing(status = ListingStatus.DRAFT, sellerAgentUserId = 9999L)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `without listing AND full_access`() {
        setupUserWithoutPermissions(listOf("listing", "listing:full_access"))

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
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
