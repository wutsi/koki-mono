package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.listing.dto.GenerateQrCodeResponse
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.WebscrapingFixtures.webpage
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
        Thread.sleep(1000)

        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-legal-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geolocation-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementNotPresent("#listing-leasing-section")
        assertElementPresent("#seller-agent-commission")
        assertElementPresent("#listing-seller-section")
        assertElementNotPresent("#listing-sale-section")

        assertElementPresent("#pills-details-tab")
        assertElementPresent("#pills-details")
        assertElementPresent("#pills-qr-code-tab")
        assertElementPresent("#pills-qr-code")

        assertElementPresent("#debug-container")
        assertElementAttribute("#debug-container a.webpage-source", "href", webpage.url)
    }

    @Test
    fun rental() {
        setupListing(listingType = ListingType.RENTAL)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)
        Thread.sleep(1000)

        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-legal-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geolocation-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementPresent("#listing-leasing-section")
        assertElementPresent("#seller-agent-commission")
        assertElementPresent("#listing-seller-section")
        assertElementNotPresent("#listing-sale-section")

        assertElementPresent("#pills-details-tab")
        assertElementPresent("#pills-details")
        assertElementPresent("#pills-qr-code-tab")
        assertElementPresent("#pills-qr-code")

        assertElementPresent("#debug-container")
    }

    @Test
    fun `view DRAFT listing`() {
        setupListing(status = ListingStatus.DRAFT)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementNotPresent("#btn-share")
        assertElementPresent("#btn-publish")
        assertElementPresent("#btn-status")

        Thread.sleep(1000)
        assertElementCount(".btn-section-edit", 9)

        assertElementNotPresent("#btn-whatsapp")
        assertElementNotPresent("#btn-call")
        assertElementNotPresent("#btn-offer")
        assertElementNotPresent("#btn-whatsapp-sticky")
        assertElementNotPresent("#btn-call-sticky")
        assertElementNotPresent("#btn-offer-sticky")

        assertElementPresent("#pills-details-tab")
        assertElementPresent("#pills-details")
        assertElementNotPresent("#pills-qr-code-tab")
        assertElementNotPresent("#pills-qr-code")
    }

    @Test
    fun `view ACTIVE listing`() {
        setupListing(status = ListingStatus.ACTIVE)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementPresent("#btn-share")
        assertElementNotPresent("#btn-publish")
        assertElementPresent("#btn-status")

        Thread.sleep(1000)
        assertElementCount(".btn-section-edit", 0)

        assertElementPresent("#btn-whatsapp")
        assertElementPresent("#btn-offer")
        assertElementPresent("#btn-whatsapp-sticky")
        assertElementPresent("#btn-offer-sticky")

        assertElementPresent("#pills-details-tab")
        assertElementPresent("#pills-details")
        assertElementPresent("#pills-qr-code-tab")
        assertElementPresent("#pills-qr-code")
    }

    @Test
    fun `view ACTIVE listing - another agent`() {
        setupListing(status = ListingStatus.ACTIVE, sellerAgentUserId = users[1].id)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementNotPresent("#btn-share")
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-status")

        Thread.sleep(1000)
        assertElementCount(".btn-section-edit", 0)

        assertElementPresent("#btn-whatsapp")
        assertElementPresent("#btn-offer")
        assertElementPresent("#btn-whatsapp-sticky")
        assertElementPresent("#btn-offer-sticky")

        assertElementsAttributeSame("#btn-whatsapp", "#btn-whatsapp-sticky", "href")
        assertElementsAttributeSame("#btn-offer", "#btn-offer-sticky", "href")

        assertElementPresent("#pills-details-tab")
        assertElementPresent("#pills-details")
        assertElementPresent("#pills-qr-code-tab")
        assertElementPresent("#pills-qr-code")
    }

    @Test
    fun `view RENTED listing`() {
        setupListing(status = ListingStatus.RENTED, listingType = ListingType.RENTAL)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementPresent("#btn-share")
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-status")

        Thread.sleep(1000)
        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geolocation-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementPresent("#listing-leasing-section")
        assertElementNotPresent("#seller-agent-commission")
        assertElementNotPresent("#buyer-agent-commission")
        assertElementPresent("#listing-seller-section")
        assertElementPresent("#listing-sale-section")
        assertElementPresent("#buyer-contact-info")
        assertElementPresent("#final-seller-agent-commission")
        // assertElementPresent("#final-buyer-agent-commission")
        assertElementCount(".btn-section-edit", 0)

        assertElementPresent("#btn-whatsapp")
        assertElementNotPresent("#btn-offer")
        assertElementPresent("#btn-whatsapp-sticky")
        assertElementNotPresent("#btn-offer-sticky")

        assertElementPresent("#pills-details-tab")
        assertElementPresent("#pills-details")
        assertElementNotPresent("#pills-qr-code-tab")
        assertElementNotPresent("#pills-qr-code")
    }

    @Test
    fun `view SOLD listing from another agent`() {
        setupListing(status = ListingStatus.SOLD, listingType = ListingType.SALE, sellerAgentUserId = users[1].id)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementNotPresent("#btn-share")
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-status")

        Thread.sleep(1000)
        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geolocation-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-price-section")
        assertElementPresent("#listing-sale-section")

        assertElementNotPresent("#seller-agent-commission")
        assertElementNotPresent("#buyer-agent-commission")
        assertElementNotPresent("#listing-seller-section")
        assertElementNotPresent("#buyer-contact-info")
        assertElementNotPresent("#final-seller-agent-commission")
        assertElementCount(".btn-section-edit", 0)

        assertElementPresent("#btn-whatsapp")
        assertElementNotPresent("#btn-offer")
        assertElementPresent("#btn-whatsapp-sticky")
        assertElementNotPresent("#btn-offer-sticky")

        assertElementPresent("#pills-details-tab")
        assertElementPresent("#pills-details")
        assertElementNotPresent("#pills-qr-code-tab")
        assertElementNotPresent("#pills-qr-code")
    }

    @Test
    fun `view another agent listing ACTIVE`() {
        setupListing(status = ListingStatus.ACTIVE, sellerAgentUserId = 9999L)

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.LISTING)

        assertElementNotPresent("#btn-share")
        assertElementNotPresent("#btn-publish")
        assertElementNotPresent("#btn-status")

        Thread.sleep(1000)
        assertElementPresent("#listing-description-section")
        assertElementPresent("#listing-general-section")
        assertElementPresent("#listing-amenity-section")
        assertElementPresent("#listing-remarks-section")
        assertElementPresent("#listing-address-section")
        assertElementPresent("#listing-geolocation-section")
        assertElementPresent("#listing-price-section")
        assertElementNotPresent("#seller-agent-commission")
        assertElementNotPresent("#listing-seller-section")

        assertElementCount(".btn-section-edit", 0)
    }

    @Test
    fun `view another agent listing DRAFT`() {
        setupListing(status = ListingStatus.DRAFT, sellerAgentUserId = 9999L)

        navigateTo("/listings/${listing.id}")
    }

    @Test
    fun `without tenant_debug permission`() {
        setupUserWithoutPermissions(listOf("tenant:debug"))

        navigateTo("/listings/${listing.id}")
        assertElementNotPresent("#debug-container")
    }

    @Test
    fun `without listing AND full_access permissions`() {
        setupUserWithoutPermissions(listOf("listing", "listing:full_access"))

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun share() {
        setupListing(status = ListingStatus.ACTIVE)

        navigateTo("/listings/${listing.id}")

        click("#btn-share")
        assertElementVisible("#koki-modal")

        assertElementVisible("#btn-share-facebook")
        assertElementVisible("#btn-share-twitter")
        assertElementVisible("#btn-share-email")
    }

    @Test
    fun `qr-code`() {
        // GIVEN
        setupListing(status = ListingStatus.ACTIVE)

        // WHEN
        navigateTo("/listings/${listing.id}?tab=qr-code")

        assertElementPresent("img.qr-code")
        assertElementAttribute("img.qr-code", "src", listing.qrCodeUrl)
        assertElementPresent("#btn-download-qr-code")
        assertElementNotPresent("#btn-generate-qr-code")
    }

    @Test
    fun `generate qr-code`() {
        // GIVEN
        setupListing(status = ListingStatus.ACTIVE, qrCodeUrl = null)

        // WHEN
        navigateTo("/listings/${listing.id}?tab=qr-code")

        // THEN
        assertElementNotPresent("img.qr-code")
        assertElementNotPresent("#btn-download-qr-code")
        assertElementPresent("#btn-generate-qr-code")

        click("#btn-generate-qr-code")
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/qr-code"),
            anyOrNull(),
            eq(GenerateQrCodeResponse::class.java),
        )
        assertCurrentPageIs(PageName.LISTING)
    }

    private fun setupListing(
        status: ListingStatus = ListingStatus.ACTIVE,
        sellerAgentUserId: Long = USER_ID,
        listingType: ListingType = ListingType.RENTAL,
        qrCodeUrl: String? = listing.qrCodeUrl
    ) {
        doReturn(
            ResponseEntity(
                GetListingResponse(
                    listing.copy(
                        status = status,
                        sellerAgentUserId = sellerAgentUserId,
                        listingType = listingType,
                        qrCodeUrl = qrCodeUrl
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
