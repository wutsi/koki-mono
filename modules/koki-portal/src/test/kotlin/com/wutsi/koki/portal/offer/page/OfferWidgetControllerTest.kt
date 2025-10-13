package com.wutsi.koki.portal.offer.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.OfferFixtures.offers
import com.wutsi.koki.offer.dto.SearchOfferResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class OfferWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun widget() {
        navigateTo("/offers/widgets/active?test-mode=true")
        assertElementCount(".widget .offer", offers.size)
    }

    @Test
    fun empty() {
        doReturn(
            ResponseEntity(
                SearchOfferResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchOfferResponse::class.java)
            )

        navigateTo("/offers/widgets/active?test-mode=true")
        assertElementNotPresent(".widget")
    }
}
