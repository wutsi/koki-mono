package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.listing.dto.GetAIListingResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class ListingDebugControllerTest : AbstractPageControllerTest() {
    @Test
    fun debug() {
        // WHEN
        navigateTo("/listings/debug?id=${listing.id}")

        // THEN
        assertCurrentPageIs(PageName.LISTING_DEBUG)

        assertElementPresent("#website-container")
        assertElementPresent("#webpage-container")
        assertElementPresent("#ai-listing-container")
    }

    @Test
    fun `no ai listing`() {
        // GIVEN
        doThrow(
            IllegalStateException::class
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetAIListingResponse::class.java)
            )

        // WHEN
        navigateTo("/listings/debug?id=${listing.id}")

        // THEN
        assertCurrentPageIs(PageName.LISTING_DEBUG)

        assertElementPresent("#website-container")
        assertElementPresent("#webpage-container")
        assertElementNotPresent("#ai-listing-container")
    }

    @Test
    fun `without tenant_debug permission`() {
        // GIVEN
        setupUserWithoutPermissions(listOf("tenant:debug"))

        // WHEN
        navigateTo("/listings/debug?id=${listing.id}")

        // THEN
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
