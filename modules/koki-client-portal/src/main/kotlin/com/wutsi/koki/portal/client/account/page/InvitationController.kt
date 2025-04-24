package com.wutsi.koki.portal.client.account.page

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.client.account.form.InvitationForm
import com.wutsi.koki.portal.client.account.model.InvitationModel
import com.wutsi.koki.portal.client.account.service.AccountUserService
import com.wutsi.koki.portal.client.account.service.InvitationService
import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/invitations")
class InvitationController(
    private val service: InvitationService,
    private val accountUserService: AccountUserService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(@PathVariable id: String, model: Model): String {
        val invitation = service.invitation(id)
        val form = InvitationForm(accountId = invitation.account.id)
        return return show(invitation, form, model)
    }

    private fun show(invitation: InvitationModel, form: InvitationForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute("invitation", invitation)
        model.addAttribute("maskedEmail", mask(invitation.account.email))
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.INVITATION,
                title = "Invitation",
            )
        )
        return "accounts/invitation"
    }

    @PostMapping("/{id}/submit")
    fun submit(
        @PathVariable id: String,
        @ModelAttribute form: InvitationForm,
        model: Model
    ): String {
        val invitation = service.invitation(id)
        val error = valid(invitation, form)
        if (error != null) {
            model.addAttribute("error", error)
            return show(invitation, form, model)
        }

        try {
            val userId = accountUserService.create(form)
            model.addAttribute("userId", userId)
            model.addAttribute("invitation", invitation)
            model.addAttribute(
                "page",
                createPageModel(
                    name = PageName.INVITATION,
                    title = "Invitation",
                )
            )
            return "accounts/invited"
        } catch (ex: HttpClientErrorException) {
            val ex = toErrorResponse(ex)
            model.addAttribute("error", ex.error.code)
            return show(invitation, form, model)
        }
    }

    private fun valid(invitation: InvitationModel, form: InvitationForm): String? {
        if (form.password != form.confirm) {
            return ErrorCode.INVITATION_PASSWORD_MISMATCH
        }
        if (form.email != invitation.account.email) {
            return ErrorCode.INVITATION_INVALID_EMAIL
        }
        return null
    }

    /**
     * foo@bar.com ⇒ f**@b*r.com
     * See https://stackoverflow.com/questions/43003138/regular-expression-for-email-masking/43004381#43004381
     */
    private fun mask(email: String?): String? {
        return email?.replace(
            Regex("(?<=.)[^@](?=[^@]*?@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$)).(?=.*[^@]\\.)"),
            "*",
        )
    }
}
