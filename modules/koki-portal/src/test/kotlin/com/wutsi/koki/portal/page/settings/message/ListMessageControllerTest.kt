package com.wutsi.koki.portal.page.settings.message

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ListMessageControllerTest : AbstractPageControllerTest() {
    private val messages = listOf(
        MessageSummary(
            id = "1",
            name = "M-001",
            subject = "Message #1",
            active = true,
        ),
        MessageSummary(
            id = "2",
            name = "M-002",
            subject = "Message #3",
            active = true,
        ),
        MessageSummary(
            id = "3",
            name = "M-003",
            subject = "Message #3",
            active = false,
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchMessageResponse(messages)).whenever(kokiMessages)
            .search(
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
        assertCurrentPageIs(PageName.MESSAGE_LIST)

        assertElementCount("tr.message", messages.size)
        assertElementNotPresent(".empty")
    }

    @Test
    fun empty() {
        doReturn(SearchMessageResponse()).whenever(kokiMessages)
            .search(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/settings/messages")
        assertCurrentPageIs(PageName.MESSAGE_LIST)

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
        doReturn(GetMessageResponse(message)).whenever(kokiMessages).get(any())

        navigateTo("/settings/messages")
        click("tr.message .btn-view")
        assertCurrentPageIs(PageName.MESSAGE)
    }

    @Test
    fun edit() {
        val message = Message(
            id = "1",
            name = "M-001",
            subject = "Message #1",
            active = true,
        )
        doReturn(GetMessageResponse(message)).whenever(kokiMessages).get(any())

        navigateTo("/settings/messages")
        click("tr.message .btn-edit")
        assertCurrentPageIs(PageName.MESSAGE_EDIT)
    }

    @Test
    fun create() {
        navigateTo("/settings/messages")
        click(".btn-create")
        assertCurrentPageIs(PageName.MESSAGE_CREATE)
    }
}
