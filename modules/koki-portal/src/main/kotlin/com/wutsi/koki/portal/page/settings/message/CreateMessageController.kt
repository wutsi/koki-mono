package com.wutsi.koki.portal.page.settings.message

import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.MessageService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class CreateMessageController(private val service: MessageService) : AbstractPageController() {
    @GetMapping("/settings/messages/create")
    fun show(model: Model): String {
        return create(MessageForm(), model)
    }

    private fun create(form: MessageForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_MESSAGE_CREATE,
                title = "New Message"
            ),
        )
        return "settings/messages/create"
    }

    @PostMapping("/settings/messages/add-new")
    fun save(
        @ModelAttribute form: MessageForm,
        model: Model
    ): String {
        try {
            val messageId = service.create(form)

            model.addAttribute("message", Message(id = messageId, name = form.name))
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
            return create(form, model)
        }
    }
}
