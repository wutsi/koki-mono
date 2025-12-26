package com.wutsi.koki.portal.listing.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.dto.CreateListingResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.web.client.RestClientException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateAIListingControllerTest : AbstractPageControllerTest() {
    private val text = "Beautiful apartment for rent located in the heart of the city with 3 bedrooms and 2 bathrooms."

    @Test
    fun create() {
        navigateTo("/listings/create/ai")
        assertCurrentPageIs(PageName.LISTING_CREATE_AI)

        assertElementNotPresent(".alert-danger")
        input("#text", text)
        scrollToBottom()
        click("#btn-submit")
        val req0 = argumentCaptor<CreateAIListingRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/listings/ai"),
            req0.capture(),
            eq(CreateListingResponse::class.java),
        )
        assertEquals(text, req0.firstValue.text)
        assertNotNull(req0.firstValue.cityId)
        assertCurrentPageIs(PageName.LISTING)
    }

    @Test
    fun error() {
        doThrow(RestClientException("Failed"))
            .whenever(rest)
            .postForEntity(
                eq("$sdkBaseUrl/v1/listings/ai"),
                any(),
                eq(CreateListingResponse::class.java),
            )

        navigateTo("/listings/create/ai")
        assertCurrentPageIs(PageName.LISTING_CREATE_AI)

        input("#text", text)
        click("#btn-submit")

        assertCurrentPageIs(PageName.LISTING_CREATE_AI)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `without manage AND full_access permission`() {
        setupUserWithoutPermissions(listOf("listing:manage", "listing:full_access"))

        navigateTo("/listings/create")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
