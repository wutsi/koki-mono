package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.CreateListingResponse
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateListingControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/listings/create")
        assertCurrentPageIs(PageName.LISTING_CREATE)

        // Create
        click("#lbl-listing-type-" + ListingType.SALE)
        click("#lbl-property-type-" + PropertyType.APARTMENT)
        click("#btn-next")
        val req0 = argumentCaptor<CreateListingRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings"),
            req0.capture(),
            eq(CreateListingResponse::class.java),
        )
        assertEquals(ListingType.SALE, req0.firstValue.listingType)
        assertEquals(PropertyType.APARTMENT, req0.firstValue.propertyType)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun `without manage AND full_access permission`() {
        setupUserWithoutPermissions(listOf("listing:manage", "listing:full_access"))

        navigateTo("/listings/create")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
