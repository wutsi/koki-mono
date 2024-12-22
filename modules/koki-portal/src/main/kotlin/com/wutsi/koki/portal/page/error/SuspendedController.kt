package com.wutsi.koki.portal.page.error

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class WutsiErrorController : ErrorController, AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WutsiErrorController::class.java)
    }

    @GetMapping("/error")
    fun error(request: HttpServletRequest, model: Model): String {
        val msg = StringBuilder()
        try {
            val message = request.getAttribute("jakarta.servlet.error.message") as String?
            message?.let {
                msg.append(" error_message=$message")
                model.addAttribute("message", message)
            }

            val code = request.getAttribute("jakarta.servlet.error.status_code") as Int?
            code?.let {
                msg.append(" error_code=$code")
                model.addAttribute("code", code)
            }

            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.ERROR,
                    title = "Error"
                )
            )

            return "error/default"
        } finally {
            val ex = request.getAttribute("jakarta.servlet.error.exception") as Throwable?
            if (ex != null) {
                LOGGER.error(msg.toString(), ex)
            } else {
                LOGGER.error(msg.toString())
            }
        }
    }
}
