package com.wutsi.koki.portal.account.page

import com.wutsi.koki.portal.account.form.AccountForm
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.account.service.AttributeService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.CurrentUserHolder
import com.wutsi.koki.portal.service.UserService
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
class CreateAccountController(
    private val service: AccountService,
    private val attributeService: AttributeService,
    private val userService: UserService,
    private val currentUser: CurrentUserHolder,
    private val request: HttpServletRequest,
) : AbstractPageController() {
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

        val languages = Locale.getISOLanguages()
            .map { lang -> Locale(lang) }
            .toSet()
            .sortedBy { it.getDisplayLanguage() }
        model.addAttribute("languages", languages)

        val users = userService.users()
        model.addAttribute("users", users)

        model.addAttribute(
            "page",
            PageModel(
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

            model.addAttribute("account", AccountModel(id = accountId, name = form.name))
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.ACCOUNT_SAVED,
                    title = form.name
                )
            )
            return "accounts/saved"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(model, form)
        }
    }
}
