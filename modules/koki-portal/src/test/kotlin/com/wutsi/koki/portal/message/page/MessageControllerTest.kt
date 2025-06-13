package com.wutsi.koki.portal.message.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.MessageFixtures.message
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.UpdateMessageStatusRequest
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

class MessageControllerTest : AbstractPageControllerTest() {
    @Test
    fun `show new message`() {
        navigateTo("/messages/${message.id}?test-mode=true")

        assertElementPresent(".btn-whatsapp")
        assertElementPresent(".btn-email")
        assertElementPresent(".btn-archive")
        assertElementNotPresent(".btn-unarchive")

        val request = argumentCaptor<UpdateMessageStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/messages/${message.id}/status"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(MessageStatus.READ, request.firstValue.status)
    }

    @Test
    fun `show read message`() {
        setUpMessage(message.copy(status = MessageStatus.READ))

        navigateTo("/messages/${message.id}?test-mode=true")

        assertElementPresent(".btn-whatsapp")
        assertElementPresent(".btn-email")
        assertElementPresent(".btn-archive")
        assertElementNotPresent(".btn-unarchive")

        verify(rest, never()).postForEntity(
            any<String>(),
            any<UpdateMessageStatusRequest>(),
            eq(Any::class.java)
        )
    }

    @Test
    fun archive() {
        setUpMessage(message.copy(status = MessageStatus.READ))

        navigateTo("/messages/${message.id}?test-mode=true")

        click(".btn-archive")

        val request = argumentCaptor<UpdateMessageStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/messages/${message.id}/status"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(MessageStatus.ARCHIVED, request.firstValue.status)
    }

    @Test
    fun unarchive() {
        setUpMessage(message.copy(status = MessageStatus.ARCHIVED))

        navigateTo("/messages/${message.id}?test-mode=true")

        click(".btn-unarchive")

        val request = argumentCaptor<UpdateMessageStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/messages/${message.id}/status"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(MessageStatus.READ, request.firstValue.status)
    }

    @Test
    fun `show - without permission message`() {
        setUpUserWithoutPermissions(listOf("message"))

        navigateTo("/messages/${message.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `show - without permission message-manage`() {
        setUpUserWithoutPermissions(listOf("message"))

        navigateTo("/messages/${message.id}")
        assertElementNotPresent(".btn-archive")
        assertElementNotPresent(".btn-unarchive")
    }

    private fun setUpMessage(message: Message) {
        doReturn(
            ResponseEntity(
                GetMessageResponse(message),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetMessageResponse::class.java)
            )
    }
}
