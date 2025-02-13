package com.wutsi.koki.portal.account.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.account.form.AccountForm
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.account.service.AttributeService
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException
import java.util.Locale

@Controller
@RequiresPermission(permissions = ["account:manage"])
class CreateAccountController(
    private val service: AccountService,
    private val attributeService: AttributeService,
    private val accountTypeService: TypeService,
    private val currentUser: CurrentUserHolder,
    private val request: HttpServletRequest,
) : AbstractAccountController() {
    @GetMapping("/accounts/create")
    fun create(model: Model): String {
        val form = AccountForm(
            managedById = currentUser.id() ?: -1,
            language = LocaleContextHolder.getLocale().language,
        )
        return create(model, form)
    }

    private fun create(model: Model, form: AccountForm): String {
        model.addAttribute("form", form)

        val attributes = attributeService.attributes(
            active = true,
            limit = Integer.MAX_VALUE,
        )
        if (attributes.isNotEmpty()) {
            model.addAttribute("attributes", attributes)
        }

        val accountTypes = accountTypeService.types(
            objectType = ObjectType.ACCOUNT,
            active = true,
            limit = Integer.MAX_VALUE,
        )
        if (accountTypes.isNotEmpty()) {
            model.addAttribute("accountTypes", accountTypes)
        }

        val languages = Locale.getISOLanguages()
            .map { lang -> Locale(lang) }
            .sortedBy { lang -> lang.getDisplayLanguage() }
        model.addAttribute("languages", languages)

        val countries = Locale.getISOCountries()
            .map { country -> Locale(LocaleContextHolder.getLocale().language, country) }
            .sortedBy { country -> country.getDisplayCountry() }
        model.addAttribute("countries", countries)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ACCOUNT_CREATE,
                title = "New Account"
            )
        )
        return "accounts/create"
    }

    @PostMapping("/accounts/add-new")
    fun addNew(@ModelAttribute form: AccountForm, model: Model): String {
        try {
            val attributes = attributeService.attributes(
                active = true,
                limit = Integer.MAX_VALUE,
            )
            val accountId = service.create(
                form.copy(
                    attributes = attributes
                        .map { attr -> attr.id to request.getParameter("attribute-${attr.id}") }
                        .toMap()
                )
            )

            return "redirect:/accounts?_toast=$accountId&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(model, form)
        }
    }
}
