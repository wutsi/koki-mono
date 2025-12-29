package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.listing.dto.MutationType
import com.wutsi.koki.listing.dto.UpdateListingLegalInfoRequest
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class EditListingLegalInfoControllerTest : AbstractPageControllerTest() {
    @Test
    fun `should update all legal info fields`() {
        navigateTo("/listings/edit/legal-info?id=${listing.id}")
        assertCurrentPageIs(PageName.LISTING_EDIT_LEGAL_INFO)

        select("#landTitle", 1)
        select("#technicalFile", 1)
        select("#numberOfSigners", 3)
        select("#mutationType", 1)
        select("#transactionWithNotary", 1)
        scrollToBottom()
        click("button[type=submit]")

        val request = argumentCaptor<UpdateListingLegalInfoRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/${listing.id}/legal-info"),
            request.capture(),
            eq(Any::class.java),
        )

        assertEquals(true, request.firstValue.landTitle)
        assertEquals(true, request.firstValue.technicalFile)
        assertEquals(3, request.firstValue.numberOfSigners)
        assertEquals(MutationType.TOTAL, request.firstValue.mutationType)
        assertEquals(true, request.firstValue.transactionWithNotary)

        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun `without listing AND full_access`() {
        setupUserWithoutPermissions(listOf("listing:manage", "listing:full_access"))

        navigateTo("/listings/edit/legal-info?id=${listing.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
