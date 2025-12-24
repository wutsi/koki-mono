package com.wutsi.koki.portal.user.page.settings.role

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.RoleForm
import com.wutsi.koki.portal.user.service.RoleService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestClientException

@Controller
@RequestMapping("/settings/roles")
@RequiresPermission(["security:admin"])
class SettingsCreateRoleController(
    private val service: RoleService,
) : AbstractPageController() {
    @GetMapping("/create")
    fun create(model: Model): String {
        val form = RoleForm()
        return create(form, model)
    }

    private fun create(form: RoleForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SECURITY_SETTINGS_ROLE_CREATE,
                title = "Create Role",
            )
        )
        return "users/settings/roles/create"
    }

    @PostMapping("/add-new")
    fun addNew(
        @ModelAttribute form: RoleForm,
        model: Model
    ): String {
        try {
            val roleId = service.create(form)
            return "redirect:/settings/roles?_toast=$roleId&_ts=" + System.currentTimeMillis()
        } catch (ex: RestClientException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)
            return create(form, model)
        }
    }
}
