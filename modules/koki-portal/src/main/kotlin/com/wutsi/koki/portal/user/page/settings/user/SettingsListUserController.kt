package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.koki.portal.account.page.ListAccountController
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.user.service.RoleService
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/users")
class SettingsListUserController(
    private val service: UserService,
    private val roleService: RoleService,
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListAccountController::class.java)
    }

    @GetMapping
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "role-id") roleId: Long? = null,
        @RequestParam(required = false, name = "type") type: UserType? = null,
        @RequestParam(required = false, name = "status") status: UserStatus? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SECURITY_SETTINGS_USER_LIST,
                title = "Security Settings",
            )
        )

        val roles = roleService.roles(
            active = true,
            limit = Integer.MAX_VALUE
        )
        model.addAttribute("roles", roles)
        model.addAttribute("roleId", roleId)

        model.addAttribute("types", UserType.entries.filter { entry -> entry != UserType.UNKNOWN })
        model.addAttribute("type", type)

        model.addAttribute("statuses", UserStatus.entries)
        model.addAttribute("status", status)

        loadToast(referer, toast, timestamp, operation, model)
        more(roleId, type, status, limit, offset, model)
        return "users/settings/users/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false, name = "role-id") roleId: Long? = null,
        @RequestParam(required = false, name = "type") type: UserType? = null,
        @RequestParam(required = false, name = "status") status: UserStatus? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val users = service.users(
            roleIds = if (roleId == null || roleId == -1L) emptyList() else listOf(roleId),
            status = status,
            type = type,
            limit = limit,
            offset = offset
        )
        model.addAttribute("users", users)
        if (users.size >= limit) {
            val nextOffset = offset + limit
            var moreUrl = "/settings/users/more?limit=$limit&offset=$nextOffset"
            roleId?.let { moreUrl = "$moreUrl&role-id=$roleId" }
            status?.let { moreUrl = "$moreUrl&status=$status" }
            type?.let { moreUrl = "$moreUrl&type=$type" }
            model.addAttribute("moreUrl", moreUrl)
        }
        return "users/settings/users/more"
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
                listOf("/settings/users/$toast", "/settings/users/create")
            )
        ) {
            if (operation == "del") {
                model.addAttribute("toast", "Deleted")
            } else {
                try {
                    val user = service.user(toast, fullGraph = false)
                    model.addAttribute(
                        "toast",
                        "<a href='/settings/users/${user.id}'>${user.displayName}</a> has been saved!"
                    )
                } catch (ex: Exception) { // I
                    LOGGER.warn("Unable to load toast information for User#$toast", ex)
                }
            }
        }
    }
}
