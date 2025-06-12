package com.wutsi.koki.portal.message.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.MessageFixtures.messages
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class MessageTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")
        assertElementCount(".tab-messages .message", messages.size)
        assertElementAttribute(
            "#message-container",
            "data-refresh-url",
            "/messages/tab/more?owner-id=111&owner-type=TAX"
        )
    }

    @Test
    fun `list - more`() {
        var entries = mutableListOf<MessageSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(messages[0].copy(id = seed++))
        }
        doReturn(
            ResponseEntity(
                SearchMessageResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchMessageResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchMessageResponse(messages),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                anyOrNull<String>(),
                eq(SearchMessageResponse::class.java)
            )

        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")
        assertElementCount("tr.message", entries.size)

        scrollToBottom()
        click("#message-load-more button")
        assertElementCount("tr.message", 2 * entries.size)

        scrollToBottom()
        click("#message-load-more button")
        assertElementCount("tr.message", 2 * entries.size + messages.size)
    }

    @Test
    fun `list - without permission message`() {
        setUpUserWithoutPermissions(listOf("message"))

        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun show() {
        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")

        assertElementPresent("#message-${messages[0].id}")
        click("#message-${messages[0].id} a")

        assertElementVisible("#koki-modal")
    }

    @Test
    fun refresh() {
        var entries = mutableListOf<MessageSummary>()
        repeat(20) {
            entries.add(messages[0].copy())
        }
        doReturn(
            ResponseEntity(
                SearchMessageResponse(messages),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchMessageResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                anyOrNull<String>(),
                eq(SearchMessageResponse::class.java)
            )

        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")
        assertElementCount("tr.message", messages.size)

        click("#btn-message-refresh")
        assertElementCount("tr.message", entries.size)
    }

    @Test
    fun archived() {
        var entries = mutableListOf<MessageSummary>()
        repeat(15) {
            entries.add(messages[0].copy())
        }
        doReturn(
            ResponseEntity(
                SearchMessageResponse(messages),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchMessageResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                anyOrNull<String>(),
                eq(SearchMessageResponse::class.java)
            )

        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")
        assertElementCount("tr.message", messages.size)

        select("#message-folder", 1)
        Thread.sleep(1000)
        assertElementCount("tr.message", entries.size)
    }
}
