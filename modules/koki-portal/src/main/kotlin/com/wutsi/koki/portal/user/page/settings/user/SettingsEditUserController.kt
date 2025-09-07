package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserForm
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.RoleService
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/settings/users")
@RequiresPermission(["security:admin"])
class SettingsEditUserController(
    private val service: UserService,
    private val roleService: RoleService,
) : AbstractPageController() {
    @GetMapping("/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val user = service.get(id)
        val form = UserForm(
            displayName = user.displayName,
            username = user.username,
            email = user.email,
            status = user.status,
            language = user.language,
            roleIds = user.roles.map { role -> role.id }
        )
        return edit(user, form, model)
    }

    private fun edit(user: UserModel, form: UserForm, model: Model): String {
        model.addAttribute("me", user)
        model.addAttribute("form", form)

        model.addAttribute(
            "roles",
            roleService.roles(limit = Integer.MAX_VALUE)
        )

        loadLanguages(model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SECURITY_SETTINGS_USER_EDIT,
                title = user.displayName ?: "-",
            )
        )

        return "users/settings/users/edit"
    }

    @PostMapping("/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: UserForm,
        model: Model
    ): String {
        try {
            service.update(id, form)
            return "redirect:/settings/users/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)
            return edit(UserModel(id = id), form, model)
        }
    }
}
