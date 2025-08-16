package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.signup.form.SignupForm
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/signup")
class SignupController : AbstractSignupController() {
    @GetMapping
    fun index(model: Model): String {
        return index(SignupForm(), model)
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
