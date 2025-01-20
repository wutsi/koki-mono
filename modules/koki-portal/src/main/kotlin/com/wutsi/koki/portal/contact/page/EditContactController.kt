package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.contact.form.ContactForm
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.contact.service.ContactTypeService
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class EditContactController(
    private val service: ContactService,
    private val contactTypeService: ContactTypeService,
) : AbstractContactController() {
    @GetMapping("/contacts/{id}/edit")
    fun edit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val contact = service.contact(id)

        val form = ContactForm(
            salutation = contact.salutation,
            profession = contact.profession,
            contactTypeId = contact.contactType?.id ?: -1,
            gender = contact.gender,
            employer = contact.employer,
            phone = contact.phone,
            mobile = contact.mobile,
            lastName = contact.lastName,
            firstName = contact.firstName,
            email = contact.email,
            accountId = contact.account?.id ?: -1,
        )

        return edit(contact, form, model)
    }

    fun edit(contact: ContactModel, form: ContactForm, model: Model): String {
        model.addAttribute("contact", contact)
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.CONTACT_EDIT,
                title = contact.name,
            )
        )

        val contactTypes = contactTypeService.contactTypes(
            limit = Integer.MAX_VALUE
        ).filter { contactType ->
            contactType.active || (contactType.id == contact.contactType?.id)
        }
        if (contactTypes.isNotEmpty()) {
            model.addAttribute("contactTypes", contactTypes)
        }

        return "contacts/edit"
    }

    @PostMapping("/contacts/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: ContactForm,
        model: Model
    ): String {
        val contact = ContactModel(
            id = id,
            firstName = form.firstName,
            lastName = form.lastName,
            salutation = form.salutation
        )
        try {
            service.update(id, form)

            model.addAttribute("contact", contact)
            model.addAttribute(
                "page",
                createPageModel(
                    name = PageName.CONTACT_SAVED,
                    title = contact.name
                )
            )
            return "contacts/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(contact, form, model)
        }
    }
}
