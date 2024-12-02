package com.wutsi.koki.portal.page.message

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.MessageService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ListMessageController(private val service: MessageService) {
    @GetMapping("/messages")
    fun list(model: Model): String {
        val messages = service.messages()
        if (messages.isNotEmpty()) {
            model.addAttribute("messages", messages)
        }
        model.addAttribute(
            "page",
            PageModel(name = PageName.MESSAGE_LIST, title = "Messages"),
        )
        return "messages/list"
    }
}
