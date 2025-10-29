package com.wutsi.koki.portal.user.page.settings.invitation

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.InvitationForm
import com.wutsi.koki.portal.user.service.InvitationService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/settings/invitations/create")
@RequiresPermission(["security:admin"])
class SettingsCreateInvitationController(
    private val service: InvitationService,
) : AbstractPageController() {
    @GetMapping
    fun create(model: Model): String {
        val form = InvitationForm()
        return create(form, model)
    }

    private fun create(form: InvitationForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SECURITY_SETTINGS_INVITATION_CREATE,
                title = "Send Invitation",
            )
        )
        return "users/settings/invitations/create"
    }

    @PostMapping
    fun addNew(
        @ModelAttribute form: InvitationForm,
        model: Model,
    ): String {
        try {
            val invitationId = service.create(form)
            return "redirect:/settings/invitations?_toast=$invitationId&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", toErrorMessage(response))
            return create(form, model)
        }
    }
}
