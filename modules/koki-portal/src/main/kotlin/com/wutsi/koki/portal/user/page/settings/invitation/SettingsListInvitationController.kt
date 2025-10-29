package com.wutsi.koki.portal.user.page.settings.invitation

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.service.InvitationService
import com.wutsi.koki.tenant.dto.InvitationStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/invitations")
@RequiresPermission(["security:admin"])
class SettingsListInvitationController(
    private val invitationService: InvitationService
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SettingsListInvitationController::class.java)
    }

    @GetMapping
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        @RequestParam(required = false, name = "_toast") toast: String? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        model: Model
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SECURITY_SETTINGS_INVITATION_LIST,
                title = "Invitations",
            )

        )
        more(limit, offset, model)
        loadToast(referer, toast, timestamp, operation, model)
        return "users/settings/invitations/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val invitations = invitationService.search(
            statuses = listOf(InvitationStatus.PENDING),
            limit = limit,
            offset = offset
        )
        if (invitations.isNotEmpty()) {
            model.addAttribute("invitations", invitations)
            if (invitations.size >= limit) {
                val nextOffset = offset + limit
                val moreUrl = "/settings/invitations/more?limit=$limit&offset=$nextOffset"
                model.addAttribute("moreUrl", moreUrl)
            }
        }
        return "users/settings/invitations/more"
    }

    @GetMapping("/delete")
    fun delete(@RequestParam id: String): String {
        invitationService.delete(id)
        return "redirect:/settings/invitations?_op=del&_toast=$id&_ts=" + System.currentTimeMillis()
    }

    private fun loadToast(
        referer: String?,
        toast: String?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (
            toast != null &&
            canShowToasts(timestamp, referer, listOf("/settings/invitations", "/settings/invitations/create"))
        ) {
            if (operation == "del") {
                model.addAttribute("toast", "Invitation deleted")
            } else {
                try {
                    val invitation = invitationService.get(toast)
                    model.addAttribute(
                        "toast",
                        "Invitation sent to ${invitation.displayName}<${invitation.email}>"
                    )
                } catch (ex: Exception) { // I
                    LOGGER.warn("Unable to load toast information for Role#$toast", ex)
                }
            }
        }
    }
}
