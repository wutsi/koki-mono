package com.wutsi.koki.portal.page.settings.message

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
    @GetMapping("/settings/messages/{id}")
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
                name = PageName.SETTINGS_MESSAGE,
                title = message.name
            ),
        )
        return "settings/messages/show"
    }

    @GetMapping("/settings/messages/{id}/delete")
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
                    name = PageName.SETTINGS_MESSAGE_DELETED,
                    title = message.name
                ),
            )
            return "settings/messages/deleted"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(message, model)
        }
    }
}
