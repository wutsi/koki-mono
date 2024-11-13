package com.wutsi.koki.portal.page.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.page.auth.LoginForm
import com.wutsi.koki.portal.rest.AuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class LoginController(
    private val authenticationService: AuthenticationService,
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoginController::class.java)
    }

    @GetMapping("/login")
    fun show(model: Model): String {
        model.addAttribute("form", LoginForm())
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.LOGIN,
                title = "Login",
            )
        )
        return "auth/login"
    }

    @PostMapping("/login/submit")
    fun submit(@ModelAttribute form: LoginForm, model: Model): String {
        try {
            authenticationService.login(form)
            return "redirect:/"
        } catch (ex: HttpClientErrorException) {
            LOGGER.warn("Authentication failed", ex)
            model.addAttribute("failed", true)
            return show(model)
        }
    }

    @ModelAttribute("page")
    fun getPage() = PageModel(
        name = PageName.LOGIN,
        title = "Login",
    )
}
