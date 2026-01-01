package com.wutsi.koki.portal.pub.neighbourhood.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.place.dto.SearchPlaceResponse
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.PlaceFixtures.place
import com.wutsi.koki.portal.pub.RefDataFixtures.neighborhoods
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.refdata.dto.GetLocationResponse
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class NeighborhoodControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                GetLocationResponse(neighborhoods[0]),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(GetLocationResponse::class.java)
            )
    }

    @Test
    fun show() {
        navigateTo("/neighbourhoods/${neighborhoods[0].id}")

        // Meta
        assertElementAttribute("html", "lang", "fr")
        assertElementAttribute(
            "head meta[name='description']",
            "content",
            place.summaryFr
        )

        // Opengraph
        assertElementAttribute("head meta[property='og:title']", "content", neighborhoods[0].name)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:description']",
            "content",
            place.summaryFr
        )
        assertElementAttributeEndsWith(
            "head meta[property='og:url']",
            "content",
            "/neighbourhoods/${neighborhoods[0].id}" + StringUtils.toSlug("", neighborhoods[0].name)
        )

        assertCurrentPageIs(PageName.NEIGHBOURHOOD)
        assertElementPresent("#introduction-container")
        assertElementPresent("#agent-container")
        assertElementPresent("#rental-listing-container")
        assertElementPresent("#sale-listing-container")
        assertElementPresent("#sold-listing-container")
        assertElementPresent("#map-container")
        assertElementPresent("#about-container")
        assertElementPresent("#school-container")
    }

    @Test
    fun `no content`() {
        doReturn(
            ResponseEntity(
                SearchPlaceResponse(emptyList()),
                HttpStatus.OK,
            )
        ).whenever(restWithoutTenantHeader)
            .getForEntity(
                any<String>(),
                eq(SearchPlaceResponse::class.java)
            )

        navigateTo("/neighbourhoods/${neighborhoods[0].id}")

        // Meta
        assertElementAttribute("html", "lang", "fr")
        assertElementNotPresent("head meta[name='description']")

        // Opengraph
        assertElementAttribute("head meta[property='og:title']", "content", neighborhoods[0].name)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementNotPresent("head meta[property='og:description']")
        assertElementAttributeEndsWith(
            "head meta[property='og:url']",
            "content",
            "/neighbourhoods/${neighborhoods[0].id}" + StringUtils.toSlug("", neighborhoods[0].name)
        )

        assertCurrentPageIs(PageName.NEIGHBOURHOOD)
        assertElementPresent("#agent-container")
        assertElementPresent("#rental-listing-container")
        assertElementPresent("#sale-listing-container")
        assertElementPresent("#sold-listing-container")
        assertElementPresent("#map-container")
        assertElementNotPresent("#about-container")
        assertElementNotPresent("#introduction-container")
    }
}
