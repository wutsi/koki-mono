package com.wutsi.koki.portal.page.message

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ShowMessageControllerTest : AbstractPageControllerTest() {
    val message = Message(
        id = "1",
        name = "M-001",
        subject = "Message #1",
        active = true,
        body = """
            <p>
                Hello <b>{{recipient}}</b>
            </p>
            <p>
                Welcome to Koki!!!
            </p>
        """.trimIndent()
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetMessageResponse(message)).whenever(kokiMessages).get(any())
    }

    @Test
    fun show() {
        navigateTo("/messages/${message.id}")
        assertCurrentPageIs(PageName.MESSAGE)
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/messages/${message.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/messages/${message.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiMessages).delete(message.id)
        assertCurrentPageIs(PageName.MESSAGE_DELETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.MESSAGE_LIST)
    }

    @Test
    fun `dismiss delete`() {
        navigateTo("/messages/${message.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(kokiMessages, never()).delete(any())
        assertCurrentPageIs(PageName.MESSAGE)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.MESSAGE_IN_USE)
        doThrow(ex).whenever(kokiMessages).delete(any())

        navigateTo("/messages/${message.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.MESSAGE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/messages/${message.id}")
        click(".btn-edit")

        assertCurrentPageIs(PageName.MESSAGE_EDIT)
    }
}
