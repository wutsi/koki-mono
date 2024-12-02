package com.wutsi.koki.portal.page.message

import com.wutsi.koki.portal.model.MessageModel
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.MessageService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.client.HttpClientErrorException

@Controller
class ShowMessageController(private val service: MessageService) : AbstractPageController() {
    @GetMapping("/messages/{id}")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val message = service.message(id)
        return show(message, model)
    }

    private fun show(message: MessageModel, model: Model): String {
        model.addAttribute("message", message)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.MESSAGE,
                title = message.name
            ),
        )
        return "messages/show"
    }

    @GetMapping("/messages/{id}/delete")
    fun delete(
        @PathVariable id: String,
        model: Model
    ): String {
        val message = service.message(id)
        try {
            service.delete(id)

            model.addAttribute("message", message)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.MESSAGE_DELETED,
                    title = message.name
                ),
            )
            return "messages/deleted"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(message, model)
        }
    }
}
