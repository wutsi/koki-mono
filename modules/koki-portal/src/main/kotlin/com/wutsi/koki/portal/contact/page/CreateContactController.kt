package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.contact.form.ContactForm
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["contact:manage"])
class CreateContactController(
    private val service: ContactService,
    private val accountService: AccountService,
    private val typeService: TypeService,
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
            language = account?.language ?: LocaleContextHolder.getLocale().language
        )
        return create(form, model)
    }

    fun create(form: ContactForm, model: Model): String {
        loadLanguages(model)
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.CONTACT_CREATE,
                title = "New Contact",
            )
        )

        val contactTypes = typeService.types(
            objectType = ObjectType.CONTACT,
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
            return "redirect:/contacts?_toast=$contactId&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, model)
        }
    }
}
