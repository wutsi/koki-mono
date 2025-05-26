package com.wutsi.koki.portal.message.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.MessageFixtures.messages
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.message.dto.UpdateMessageStatusRequest
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

class MessageTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")
        assertElementCount(".tab-messages .message", messages.size)
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
    fun archive() {
        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")

        assertElementPresent("#message-${messages[0].id}")
        click("#btn-archive-${messages[0].id}")
        assertElementNotPresent("#message-${messages[0].id}")

        val request = argumentCaptor<UpdateMessageStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/messages/${messages[0].id}/status"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(MessageStatus.ARCHIVED, request.firstValue.status)
    }

    @Test
    fun unarchive() {
        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")

        assertElementPresent("#message-${messages[1].id}")
        click("#btn-unarchive-${messages[1].id}")
        assertElementNotPresent("#message-${messages[1].id}")

        val request = argumentCaptor<UpdateMessageStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/messages/${messages[1].id}/status"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(MessageStatus.NEW, request.firstValue.status)
    }

    @Test
    fun `list - without permission message`() {
        setUpUserWithoutPermissions(listOf("message"))

        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `list - without permission message-manage`() {
        setUpUserWithoutPermissions(listOf("message"))

        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")
        assertElementNotPresent(".btn-archive")
        assertElementNotPresent(".btn-unarchive")
    }
}
