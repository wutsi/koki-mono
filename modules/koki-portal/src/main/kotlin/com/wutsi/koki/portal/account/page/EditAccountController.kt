package com.wutsi.koki.portal.account.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.account.form.AccountForm
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.account.service.AttributeService
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import com.wutsi.koki.portal.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException
import java.util.Locale

@Controller
@RequiresPermission(permissions = ["account:manage"])
class EditAccountController(
    private val service: AccountService,
    private val attributeService: AttributeService,
    private val typeService: TypeService,
    private val userService: UserService,
    private val request: HttpServletRequest,
) : AbstractAccountController() {
    @GetMapping("/accounts/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val account = service.account(id)
        val form = AccountForm(
            managedById = account.managedBy?.id,
            accountTypeId = account.accountType?.id,
            language = account.language,
            phone = account.phone,
            description = account.description,
            website = account.website,
            email = account.email,
            mobile = account.mobile,
            name = account.name,
            attributes = account.attributes.map { attr -> attr.attribute.id to attr.value }.toMap(),
            shippingStreet = account.shippingAddress?.street,
            shippingCountry = account.shippingAddress?.country,
            shippingPostalCode = account.shippingAddress?.postalCode,
            shippingCityId = account.shippingAddress?.city?.id,
            billingStreet = account.billingAddress?.street,
            billingCountry = account.billingAddress?.country,
            billingPostalCode = account.billingAddress?.postalCode,
            billingCityId = account.billingAddress?.city?.id,
        )
        return edit(model, form, account)
    }

    private fun edit(model: Model, form: AccountForm, account: AccountModel): String {
        model.addAttribute("form", form)
        model.addAttribute("account", account)

        val attributes = attributeService.attributes(
            limit = Integer.MAX_VALUE,
        ).filter { attribute -> // All active + inactive attributes used by the account
            attribute.active || form.attributes.keys.contains(attribute.id)
        }
        if (attributes.isNotEmpty()) {
            model.addAttribute("attributes", attributes)
        }

        val accountTypes = typeService.types(
            objectType = ObjectType.ACCOUNT,
            limit = Integer.MAX_VALUE,
        ).filter { accountType -> // All active + inactive type used by the account
            accountType.active || accountType.id == account.accountType?.id
        }
        if (accountTypes.isNotEmpty()) {
            model.addAttribute("accountTypes", accountTypes)
        }

        if (form.managedById != null) {
            model.addAttribute("manager", userService.user(id = form.managedById, fullGraph = false))
        }

        val languages = Locale.getISOLanguages()
            .map { lang -> Locale(lang) }
            .toSet()
            .sortedBy { it.getDisplayLanguage() }
        model.addAttribute("languages", languages)

        val countries = Locale.getISOCountries()
            .map { country -> Locale(LocaleContextHolder.getLocale().language, country) }
            .sortedBy { country -> country.getDisplayCountry() }
        model.addAttribute("countries", countries)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ACCOUNT_EDIT,
                title = form.name
            )
        )
        return "accounts/edit"
    }

    @PostMapping("/accounts/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: AccountForm,
        model: Model
    ): String {
        val account = AccountModel(
            id = id,
            name = form.name
        )
        try {
            val attributes = attributeService.attributes(
                active = true,
                limit = Integer.MAX_VALUE,
            )

            service.update(
                id,
                form.copy(
                    attributes = attributes
                        .map { attr -> attr.id to request.getParameter("attribute-${attr.id}") }
                        .toMap()
                )
            )

            return "redirect:/accounts/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(model, form, account)
        }
    }
}
