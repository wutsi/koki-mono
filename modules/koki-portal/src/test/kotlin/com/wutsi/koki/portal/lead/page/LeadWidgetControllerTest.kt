package com.wutsi.koki.portal.lead.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.lead.dto.SearchLeadResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.LeadFixtures.leads
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class LeadWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun new() {
        navigateTo("/leads/widgets/new?test-mode=true")
        assertElementCount(".widget .lead", leads.size)
    }

    @Test
    fun empty() {
        doReturn(
            ResponseEntity(
                SearchLeadResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchLeadResponse::class.java)
            )

        navigateTo("/leads/widgets/new?test-mode=true")
        assertElementNotPresent(".widget")
    }
}
