package com.wutsi.koki.portal.security.page

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.form.LoginForm
import com.wutsi.koki.portal.security.service.LoginService
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
    private val response: HttpServletResponse,
    private val logger: KVLogger,
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoginController::class.java)
    }

    @GetMapping("/login")
    fun show(model: Model): String {
        model.addAttribute("form", LoginForm())
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LOGIN,
                title = "Login",
            )
        )
        return "security/login"
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
            val response = toErrorResponse(ex)
            if (response.error.code == ErrorCode.AUTHENTICATION_USER_NOT_ACTIVE) {
                model.addAttribute("error", getMessage("error.account-not-active"))
            } else {
                model.addAttribute("error", getMessage("error.authentication-failed"))
            }
            logger.add("backend_error", response.error.code)
            model.addAttribute("failed", true)
            return show(model)
        }
    }
}
