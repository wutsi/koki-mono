package com.wutsi.koki.portal.message.page

import com.wutsi.koki.MessageFixtures.messages
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.Test

class MessageWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/messages/widget?test-mode=true")
        assertElementCount(".message", messages.size)
        assertElementAttribute("#message-container", "data-refresh-url", "/messages/widget/body")
    }

    @Test
    fun `show - without permission message`() {
        setupUserWithoutPermissions(listOf("message"))

        navigateTo("/messages/widget?test-mode=true")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `open message`() {
        navigateTo("/messages/tab?test-mode=true&owner-id=111&owner-type=TAX")

        assertElementPresent("#message-${messages[0].id}")
        click("#message-${messages[0].id} a")

        assertElementVisible("#koki-modal")
    }
}
