package com.wutsi.koki.portal.contact.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.contact.dto.PreferredCommunicationMethod
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.contact.form.ContactForm
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import io.lettuce.core.KillArgs.Builder.id
import jdk.javadoc.internal.doclets.toolkit.util.DocPath.parent
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["contact:manage", "contact:full_access"])
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
        var account = accountId?.let { id -> accountService.account(id) }
        if (account == null) {
            val accounts = accountService.accounts(
                managedByIds = userHolder.get()?.let { user -> listOf(user.id) } ?: emptyList(),
                limit = 2,
            )
            if (accounts.size == 1) {
                account = accounts.firstOrNull()
            }
        }

        val city = resolveCity()
        val form = ContactForm(
            accountId = account?.id ?: -1,
            language = account?.language ?: LocaleContextHolder.getLocale().language,
            country = city?.country ?: tenantHolder.get()?.country,
            cityId = city?.id,
        )
        return create(form, model, city)
    }

    fun create(form: ContactForm, model: Model, city: LocationModel?): String {
        loadLanguages(model)
        loadCountries(model)
        model.addAttribute("communicationMethods", PreferredCommunicationMethod.entries)
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
        if (form.accountId > 0) {
            model.addAttribute("account", accountService.account(id = form.accountId, fullGraph = false))
        }

        val xcity = city ?: form.cityId?.let { id -> locationService.get(id) }
        if (xcity != null) {
            val parent = resolveParent(xcity)
            model.addAttribute("city", xcity)
            model.addAttribute("cityName", parent?.let { "${xcity.name}, ${parent.name}" } ?: xcity.name)
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
            return create(form, model, null)
        }
    }
}
