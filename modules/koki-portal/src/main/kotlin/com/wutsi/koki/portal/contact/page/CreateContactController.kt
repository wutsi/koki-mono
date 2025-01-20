package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.contact.form.ContactForm
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.contact.service.ContactTypeService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
class CreateContactController(
    private val service: ContactService,
    private val accountService: AccountService,
    private val contactTypeService: ContactTypeService,
) : AbstractContactController() {
    @GetMapping("/contacts/create")
    fun create(
        @RequestParam(required = false, name = "account-id") accountId: Long? = null,
        model: Model,
    ): String {
        val account = accountId?.let { id -> accountService.account(id) }
        model.addAttribute("account", account)

        val form = ContactForm(
            accountId = accountId ?: -1,
        )
        return create(form, model)
    }

    fun create(form: ContactForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.CONTACT_CREATE,
                title = "New Contact",
            )
        )

        val contactTypes = contactTypeService.contactTypes(
            active = true,
            limit = Integer.MAX_VALUE,
        )
        if (contactTypes.isNotEmpty()) {
            model.addAttribute("contactTypes", contactTypes)
        }

        return "contacts/create"
    }

    @PostMapping("/contacts/add-new")
    fun addNew(
        @ModelAttribute form: ContactForm, model: Model
    ): String {
        try {
            val contactId = service.create(form)
            val contact = ContactModel(
                id = contactId,
                firstName = form.firstName,
                lastName = form.lastName,
                salutation = form.salutation
            )
            model.addAttribute("contact", contact)
            model.addAttribute(
                "page", PageModel(
                    name = PageName.CONTACT_SAVED, title = contact.name
                )
            )
            model.addAttribute(
                "createUrl",
                "/contacts/create" + if (form.accountId == -1L) "" else "?account-id=${form.accountId}"
            )
            return "contacts/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, model)
        }
    }
}
