package com.wutsi.koki.portal.user.page.settings.role

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.model.RoleForm
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.service.RoleService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/settings/roles")
class SettingsEditRoleController(
    private val service: RoleService
) : AbstractPageController() {
    @GetMapping("/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val role = service.role(id)
        val form = RoleForm(
            name = role.name,
            title = role.title,
            description = role.description,
            active = role.active
        )
        return edit(role, form, model)
    }

    private fun edit(role: RoleModel, form: RoleForm, model: Model): String {
        model.addAttribute("role", role)
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_ROLE_EDIT,
                title = role.title,
            )

        )
        return "users/settings/roles/edit"
    }

    @PostMapping("/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: RoleForm,
        model: Model
    ): String {
        try {
            service.update(id, form)
            return "redirect:/settings/roles/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)
            return edit(RoleModel(id = id), form, model)
        }
    }
}
