package com.wutsi.koki.portal.message.page

import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.message.service.MessageService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/messages/widget")
@RequiresPermission(["message"])
class MessageWidgetController(
    private val service: MessageService,
) : AbstractPageController() {
    @GetMapping
    fun show(
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        model: Model,
    ): String {
        model.addAttribute("testMode", testMode)
        model.addAttribute("refreshUrl", "/messages/widget/body")
        model.addAttribute(
            "page",
            createPageModel(PageName.MESSAGE_WIDGET, "Messages")
        )

        body(model)
        return "messages/widget/show"
    }

    @GetMapping("/body")
    fun body(
        model: Model,
    ): String {
        val messages = service.messages(
            statuses = listOf(MessageStatus.NEW, MessageStatus.READ),
            limit = 5,
            offset = 0,
        )
        model.addAttribute("messages", messages)
        return "messages/widget/body"
    }
}
