package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.signup.form.SignupForm
import com.wutsi.koki.portal.user.service.InvitationService
import com.wutsi.koki.tenant.dto.InvitationStatus
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory.model
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.nio.charset.Charset

@Controller
@RequestMapping("/signup")
class SignupController(private val invitationService: InvitationService) : AbstractSignupController() {
    @GetMapping
    fun index(
        @RequestParam(name = "inv") invitationId: String,
        model: Model,
    ): String {
        val invitation = invitationService.get(invitationId)
        model.addAttribute("invitation", invitation)
        if (invitation.status == InvitationStatus.EXPIRED || invitation.status == InvitationStatus.ACCEPTED) {
            throw HttpClientErrorException.create(
                HttpStatusCode.valueOf(410),
                "Expired", HttpHeaders(),
                "".toByteArray(),
                Charset.defaultCharset()
            )
        }

        return index(
            SignupForm(
                invitationId = invitationId,
                name = invitation.displayName,
                email = invitation.email,
            ),
            model,
        )
    }

    private fun index(form: SignupForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SIGNUP,
                title = getMessage("page.signup.meta.title"),
            )
        )
        return "signup/index"
    }

    @PostMapping
    fun submit(@ModelAttribute form: SignupForm, model: Model): String {
        try {
            val id = signupService.create(form)
            return "redirect:/signup/profile?id=$id"
        } catch (ex: HttpClientErrorException) {
            loadError(ex, model)
            return index(form, model)
        }
    }
}
