package com.wutsi.koki.portal.page.settings.message

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.MessageService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import kotlin.collections.isNotEmpty

@Controller
class ListMessageController(private val service: MessageService) : AbstractPageController() {
    @GetMapping("/settings/messages")
    fun list(model: Model): String {
        val messages = service.messages()
        if (messages.isNotEmpty()) {
            model.addAttribute("messages", messages)
        }
        model.addAttribute(
            "page",
            PageModel(name = PageName.SETTINGS_MESSAGE_LIST, title = "Messages"),
        )
        return "settings/messages/list"
    }
}
