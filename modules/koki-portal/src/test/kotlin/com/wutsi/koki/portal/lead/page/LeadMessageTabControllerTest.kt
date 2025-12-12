package com.wutsi.koki.portal.lead.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.lead.dto.LeadMessageSummary
import com.wutsi.koki.lead.dto.SearchLeadMessageResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.LeadFixtures.messages
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class LeadMessageTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun tab() {
        navigateTo("/leads/messages/tab?owner-id=111&owner-type=LEAD&test-mode=true")

        assertElementCount(".tab-messages tr.message", messages.size)
    }

    @Test
    fun more() {
        navigateTo("/leads/messages/tab?owner-id=111&owner-type=LEAD&test-mode=true")

        val entries = mutableListOf<LeadMessageSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(messages[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchLeadMessageResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchLeadMessageResponse::class.java)
            )

        navigateTo("/leads/messages/tab?owner-id=111&owner-type=LEAD&test-mode=true")

        assertElementCount("tr.message", entries.size)

        scrollToBottom()
        click("#message-load-more button")
        assertElementCount("tr.message", 2 * entries.size)
    }
}
