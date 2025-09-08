package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.contact.dto.PreferredCommunicationMethod
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.contact.form.ContactForm
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["contact:manage", "contact:full_access"])
class EditContactController(
    private val service: ContactService,
    private val typeService: TypeService,
    private val accountService: AccountService,
) : AbstractContactController() {
    @GetMapping("/contacts/{id}/edit")
    fun edit(
        @PathVariable id: Long,
        model: Model
    ): String {
        // Check Permission
        val contact = service.get(id)
        if (!contact.canManage(userHolder.get())) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }

        // Edit
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
            preferredCommunicationMethod = contact.preferredCommunicationMethod,
            street = contact.address?.street,
            cityId = contact.address?.city?.id,
            postalCode = contact.address?.postalCode,
            country = contact.address?.country,
            language = contact.language,
        )

        return edit(contact, form, model)
    }

    fun edit(contact: ContactModel, form: ContactForm, model: Model): String {
        loadLanguages(model)
        loadCountries(model)
        model.addAttribute("communicationMethods", PreferredCommunicationMethod.entries)
        model.addAttribute("contact", contact)
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.CONTACT_EDIT,
                title = contact.name,
            )
        )

        val contactTypes = typeService.types(
            objectType = ObjectType.CONTACT,
            limit = Integer.MAX_VALUE
        ).filter { contactType ->
            contactType.active || (contactType.id == contact.contactType?.id)
        }
        if (contactTypes.isNotEmpty()) {
            model.addAttribute("contactTypes", contactTypes)
        }
        if (form.accountId > 0) {
            model.addAttribute("account", accountService.account(id = form.accountId, fullGraph = false))
        }

        val city = form.cityId?.let { id -> locationService.get(id) }
        if (city != null) {
            val parent = resolveParent(city)
            model.addAttribute("city", city)
            model.addAttribute("cityName", parent?.let { "${city.name}, ${parent.name}" } ?: city.name)
        }

        return "contacts/edit"
    }

    @PostMapping("/contacts/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: ContactForm,
        model: Model
    ): String {
        // Check Permission
        val contact = service.get(id)
        if (!contact.canDelete(userHolder.get())) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }

        // Update
        try {
            service.update(id, form)
            return "redirect:/contacts/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(contact, form, model)
        }
    }
}
