package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.model.UserRoleForm
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
class SettingsEditUserRoleController(
    private val service: UserService,
    private val roleService: RoleService,
) : AbstractPageController() {
    @GetMapping("/{id}/roles")
    fun edit(@PathVariable id: Long, model: Model): String {
        val user = service.user(id)
        val form = UserRoleForm(
            roleId = user.roles.map { role -> role.id }
        )
        return edit(user, form, model)
    }

    private fun edit(user: UserModel, form: UserRoleForm, model: Model): String {
        model.addAttribute("me", user)
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_USER_ROLE,
                title = user.displayName,
            )
        )

        val roles = roleService.roles(
            active = true,
            limit = Integer.MAX_VALUE,
        )
        model.addAttribute("roles", roles)
        return "users/settings/users/roles"
    }

    @PostMapping("/{id}/roles")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: UserRoleForm,
        model: Model
    ): String {
        try {
            service.setRoles(id, form)
            return "redirect:/settings/users/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)

            val user = service.user(id)
            return edit(user, form, model)
        }
    }
}
