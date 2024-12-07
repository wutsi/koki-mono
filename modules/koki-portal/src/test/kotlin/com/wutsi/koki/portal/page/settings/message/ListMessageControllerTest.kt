package com.wutsi.koki.portal.page.settings.message

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.MessageFixtures.messages
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ListMessageControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchMessageResponse(messages)).whenever(kokiMessages)
            .messages(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    @Test
    fun list() {
        navigateTo("/settings/messages")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_LIST)

        assertElementCount("tr.message", messages.size)
        assertElementNotPresent(".empty")
    }

    @Test
    fun empty() {
        doReturn(SearchMessageResponse()).whenever(kokiMessages)
            .messages(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/settings/messages")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_LIST)

        assertElementNotPresent("tr.message")
        assertElementPresent(".empty")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings/messages")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun view() {
        val message = Message(
            id = "1",
            name = "M-001",
            subject = "Message #1",
            active = true,
        )
        doReturn(GetMessageResponse(message)).whenever(kokiMessages).message(any())

        navigateTo("/settings/messages")
        click("tr.message .btn-view")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE)
    }

    @Test
    fun edit() {
        val message = Message(
            id = "1",
            name = "M-001",
            subject = "Message #1",
            active = true,
        )
        doReturn(GetMessageResponse(message)).whenever(kokiMessages).message(any())

        navigateTo("/settings/messages")
        click("tr.message .btn-edit")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_EDIT)
    }

    @Test
    fun create() {
        navigateTo("/settings/messages")
        click(".btn-create")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_CREATE)
    }
}
