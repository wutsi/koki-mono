package com.wutsi.koki.portal.user.page.settings.role

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.portal.user.service.RoleService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/roles")
@RequiresPermission(["security:admin"])
class SettingsRoleController(
    private val roleService: RoleService,
    private val configurationService: ConfigurationService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @PathVariable id: Long,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        val role = roleService.role(id)

        val configs = configurationService.configurations(listOf(ConfigurationName.PORTAL_SIGNUP_ROLE_ID))
        val portalSignupRoleId = configs[ConfigurationName.PORTAL_SIGNUP_ROLE_ID]
        model.addAttribute("portalSignupRoleId", portalSignupRoleId)

        if (portalSignupRoleId != id.toString()) {
            model.addAttribute("portalSignupRoleUrl", "/settings/roles/$id/portal-signup-role")
        }

        loadToast(id, referer, toast, timestamp, model)
        return show(role, model)
    }

    @GetMapping("/{id}/portal-signup-role")
    fun portalSignupRole(@PathVariable id: Long): String {
        val role = roleService.role(id)
        configurationService.save(
            mapOf(
                ConfigurationName.PORTAL_SIGNUP_ROLE_ID to role.id.toString(),
            )
        )
        return "redirect:/settings/roles/$id"
    }

    private fun show(role: RoleModel, model: Model): String {
        model.addAttribute("role", role)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SECURITY_SETTINGS_ROLE,
                title = role.title ?: role.name,
            )

        )
        return "users/settings/roles/show"
    }

    private fun loadToast(
        id: Long,
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (
            toast == id &&
            canShowToasts(timestamp, referer, listOf("/settings/roles/$id/edit", "/settings/roles/$id/permissions"))
        ) {
            model.addAttribute("toast", "Saved")
        }
    }
}
