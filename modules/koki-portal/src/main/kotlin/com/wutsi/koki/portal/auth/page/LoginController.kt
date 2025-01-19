package com.wutsi.koki.portal.auth.page

import com.wutsi.koki.portal.auth.form.LoginForm
import com.wutsi.koki.portal.auth.service.LoginService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class LoginController(
    private val service: LoginService,
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
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
            // Login
            service.login(form)

            // Redirect
            val requestCache: RequestCache = HttpSessionRequestCache()
            val savedRequest = requestCache.getRequest(request, response)
            if (savedRequest != null) {
                return "redirect:${savedRequest.redirectUrl}"
            } else {
                return "redirect:/"
            }
        } catch (ex: HttpClientErrorException) {
            LOGGER.warn("Authentication failed", ex)
            model.addAttribute("failed", true)
            return show(model)
        }
    }
}
