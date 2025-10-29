package com.wutsi.koki.portal.error.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ListingFixtures.listing
import com.wutsi.koki.listing.dto.GetListingResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.Test

class WutsiErrorControllerTest : AbstractPageControllerTest() {
    @Test
    fun `invalid page`() {
        navigateTo("/this/is/invalid/page")
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `downstream - 410`() {
        doThrow(HttpClientErrorException(HttpStatusCode.valueOf(410)))
            .whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_410)
    }

    @Test
    fun `downstream - 404`() {
        doThrow(HttpClientErrorException(HttpStatusCode.valueOf(404)))
            .whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `downstream - 400`() {
        doThrow(HttpClientErrorException(HttpStatusCode.valueOf(400)))
            .whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `downstream - 500`() {
        doThrow(HttpClientErrorException(HttpStatusCode.valueOf(500)))
            .whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_500)
    }

    @Test
    fun `downstream - Exception`() {
        doThrow(IllegalStateException::class)
            .whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetListingResponse::class.java)
            )

        navigateTo("/listings/${listing.id}")
        assertCurrentPageIs(PageName.ERROR_500)
    }
}
