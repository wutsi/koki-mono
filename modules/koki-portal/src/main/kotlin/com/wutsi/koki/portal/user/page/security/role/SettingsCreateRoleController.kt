package com.wutsi.koki.portal.user.page.security

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.service.RoleService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SettingsListRoleController(
    private val service: RoleService
) : AbstractPageController() {
    @GetMapping("/settings/security/roles")
    fun show(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_ROLE,
                title = "Security Settings",
            )

        )
        more(limit, offset, model)
        return "users/settings/role/list"
    }

    @GetMapping("/settings/security/roles/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val roles = service.roles(
            limit = limit,
            offset = offset
        )
        model.addAttribute("roles", roles)
        if (roles.size >= limit) {
            val nextOffset = offset + limit
            val moreUrl = "/settings/security/roles/more?limit=$limit&offset=$nextOffset"
            model.addAttribute("moreUrl", moreUrl)
        }
        return "users/settings/role/more"
    }
}
