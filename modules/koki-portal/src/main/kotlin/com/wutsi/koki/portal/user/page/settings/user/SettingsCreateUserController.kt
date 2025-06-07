package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserForm
import com.wutsi.koki.portal.user.service.RoleService
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/settings/users")
@RequiresPermission(["security:admin"])
class SettingsCreateUserController(
    private val service: UserService,
    private val roleService: RoleService,
) : AbstractPageController() {
    @GetMapping("/create")
    fun create(model: Model): String {
        val form = UserForm(
            language = LocaleContextHolder.getLocale().language
        )
        return create(form, model)
    }

    private fun create(form: UserForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "roles",
            roleService.roles(limit = Integer.MAX_VALUE)
        )

        loadLanguages(model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SECURITY_SETTINGS_USER_CREATE,
                title = "Create AccountUser",
            )
        )
        return "users/settings/users/create"
    }

    @PostMapping("/add-new")
    fun addNew(
        @ModelAttribute form: UserForm,
        model: Model
    ): String {
        try {
            val userId = service.create(form)
            return "redirect:/settings/users?_toast=$userId&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)
            return create(form, model)
        }
    }
}
