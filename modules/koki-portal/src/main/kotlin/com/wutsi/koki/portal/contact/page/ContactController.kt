package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.client.HttpClientErrorException

@Controller
class ContactController(
    private val service: ContactService
) : AbstractContactDetailsController() {
    @GetMapping("/contacts/{id}")
    fun show(
        @PathVariable id: Long,
        model: Model
    ): String {
        val contact = service.contact(id)
        return show(contact, model)
    }

    private fun show(contact: ContactModel, model: Model): String {
        model.addAttribute("contact", contact)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.CONTACT,
                title = "Contacts",
            )
        )
        return "contacts/show"
    }

    @GetMapping("/contacts/{id}/delete")
    fun delete(@PathVariable id: Long, model: Model): String {
        val contact = service.contact(id)
        try {
            service.delete(id)

            model.addAttribute("contact", contact)
            model.addAttribute(
                "page",
                createPageModel(
                    name = PageName.CONTACT_DELETED,
                    title = contact.name,
                )
            )
            return "contacts/deleted"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(contact, model)
        }
    }
}
