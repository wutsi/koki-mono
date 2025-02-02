package com.wutsi.koki.portal.page.settings.message

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.message.dto.CreateMessageRequest
import com.wutsi.koki.message.dto.CreateMessageResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateMessageControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(CreateMessageResponse("1111")).whenever(kokiMessages).create(any())
    }

    @Test
    fun create() {
        navigateTo("/settings/messages/create")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_CREATE)

        input("input[name=name]", "M-XXX")
        input("input[name=subject]", "This is the new subject")
        input("textarea[name=description]", "This is the description")
        input("textarea[name=body]", "<p>Looks good :-)</p>")
        scrollToBottom()
        select("select[name=active]", 1)
        click("button[type=submit]")

        val request = argumentCaptor<CreateMessageRequest>()
        verify(kokiMessages).create(request.capture())

        assertEquals("M-XXX", request.firstValue.name)
        assertEquals("This is the new subject", request.firstValue.subject)
        assertEquals("<p>Looks good :-)</p>", request.firstValue.body)
        assertEquals("This is the description", request.firstValue.description)
        assertEquals(false, request.firstValue.active)

        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_SAVED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_LIST)
    }

    @Test
    fun cancel() {
        navigateTo("/settings/messages/create")

        input("input[name=name]", "M-XXX")
        input("input[name=subject]", "This is the new subject")
        input("textarea[name=description]", "This is the description")
        input("textarea[name=body]", "<p>Looks good :-)</p>")
        scrollToBottom()
        select("select[name=active]", 1)
        click(".btn-cancel")

        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.MESSAGE_IN_USE)
        doThrow(ex).whenever(kokiMessages).create(any())

        navigateTo("/settings/messages/create")

        input("input[name=name]", "M-XXX")
        input("input[name=subject]", "This is the new subject")
        input("textarea[name=body]", "<p>Looks good :-)</p>")
        scrollToBottom()
        select("select[name=active]", 1)
        click("button[type=submit]")

        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/messages/create")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
