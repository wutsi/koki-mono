package com.wutsi.koki.portal.user.page.settings.role

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.service.RoleService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/roles")
class SettingsListRoleController(
    private val service: RoleService,
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SettingsListRoleController::class.java)
    }

    @GetMapping
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_ROLE_LIST,
                title = "Security Settings",
            )

        )
        loadToast(referer, toast, timestamp, operation, model)
        more(limit, offset, model)
        return "users/settings/roles/list"
    }

    @GetMapping("/more")
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
            val moreUrl = "/settings/roles/more?limit=$limit&offset=$nextOffset"
            model.addAttribute("moreUrl", moreUrl)
        }
        return "users/settings/roles/more"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (toast != null && canShowToasts(
                timestamp,
                referer,
                listOf("/settings/roles/$toast", "/settings/roles/create")
            )
        ) {
            if (operation == "del") {
                model.addAttribute("toast", "Deleted")
            } else {
                try {
                    val role = service.role(toast)
                    model.addAttribute(
                        "toast",
                        "<a href='/settings/roles/${role.id}'>${role.title}</a> has been saved!"
                    )
                } catch (ex: Exception) { // I
                    LOGGER.warn("Unable to load toast information for Role#$toast", ex)
                }
            }
        }
    }
}
