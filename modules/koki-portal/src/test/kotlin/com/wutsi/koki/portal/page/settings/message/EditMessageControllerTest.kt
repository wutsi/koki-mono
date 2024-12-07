package com.wutsi.koki.portal.page.settings.message

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.MessageFixtures.message
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.UpdateMessageRequest
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class EditMessageControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetMessageResponse(message)).whenever(kokiMessages).message(any())
    }

    @Test
    fun edit() {
        navigateTo("/settings/messages/${message.id}/edit")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_EDIT)

        input("input[name=name]", "M-XXX")
        input("input[name=subject]", "This is the new subject")
        input("textarea[name=body]", "<p>Looks good :-)</p>")
        scrollToBottom()
        select("select[name=active]", 1)
        click("button[type=submit]")

        val request = argumentCaptor<UpdateMessageRequest>()
        verify(kokiMessages).update(eq(message.id), request.capture())

        assertEquals("M-XXX", request.firstValue.name)
        assertEquals("This is the new subject", request.firstValue.subject)
        assertEquals("<p>Looks good :-)</p>", request.firstValue.body)
        assertEquals(false, request.firstValue.active)

        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_SAVED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_LIST)
    }

    @Test
    fun cancel() {
        navigateTo("/settings/messages/${message.id}/edit")

        scrollToBottom()
        click(".btn-cancel")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.MESSAGE_IN_USE)
        doThrow(ex).whenever(kokiMessages).update(any(), any())

        navigateTo("/settings/messages/${message.id}/edit")

        scrollToBottom()
        click("button[type=submit]")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/messages/${message.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
