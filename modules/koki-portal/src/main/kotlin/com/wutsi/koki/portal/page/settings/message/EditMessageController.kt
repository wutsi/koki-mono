package com.wutsi.koki.portal.page.settings.message

import com.wutsi.koki.portal.model.MessageModel
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.MessageService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class EditMessageController(private val service: MessageService) : AbstractPageController() {
    @GetMapping("/settings/messages/{id}/edit")
    fun edit(
        @PathVariable id: String,
        model: Model
    ): String {
        val message = service.message(id)
        val form = MessageForm(
            name = message.name,
            subject = message.subject,
            body = message.body,
            active = message.active,
            description = message.description ?: "",
        )
        return edit(form, message, model)
    }

    private fun edit(form: MessageForm, message: MessageModel, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute("message", message)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_MESSAGE_EDIT,
                title = message.name
            ),
        )
        return "settings/messages/edit"
    }

    @PostMapping("/settings/messages/{id}/update")
    fun save(
        @PathVariable id: String,
        @ModelAttribute form: MessageForm,
        model: Model
    ): String {
        val message = MessageModel(id = id, name = form.name)
        try {
            service.update(id, form)

            model.addAttribute("message", message)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_MESSAGE_SAVED,
                    title = form.name,
                ),
            )
            return "settings/messages/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, message, model)
        }
    }
}
