package com.wutsi.koki.portal.error.page

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException
import java.lang.StringBuilder
import kotlin.jvm.java
import kotlin.let

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

            val ex = getException(request)
            if (ex != null) {
                return handleException(ex, model)
            } else {
                val code = request.getAttribute("jakarta.servlet.error.status_code") as Int?
                return handleStatusCode(code, model)
            }
        } finally {
            val ex = request.getAttribute("jakarta.servlet.error.exception") as Throwable?
            if (ex != null) {
                LOGGER.error(msg.toString(), ex)
            } else {
                LOGGER.error(msg.toString())
            }
        }
    }

    private fun handleException(ex: Throwable, model: Model): String {
        return if (ex is HttpClientErrorException) {
            handleStatusCode(ex.statusCode.value(), model)
        } else {
            handleStatusCode(500, model)
        }
    }

    private fun handleStatusCode(code: Int?, model: Model): String {
        when (code) {
            403 -> {
                model.addAttribute(
                    "page",
                    PageModel(
                        name = PageName.ERROR_403,
                        title = "Access Denied"
                    )
                )
                return "error/403"
            }

            400, 404 -> {
                model.addAttribute(
                    "page",
                    PageModel(
                        name = PageName.ERROR_404,
                        title = "Not Found"
                    )
                )
                return "error/404"
            }

            else -> {
                model.addAttribute(
                    "page",
                    PageModel(
                        name = PageName.ERROR_500,
                        title = "Error"
                    )
                )
                return "error/500"
            }
        }
    }

    private fun getException(request: HttpServletRequest): Throwable? {
        val ex = request.getAttribute("jakarta.servlet.error.exception") as Throwable?
        return if (ex is ServletException) {
            ex.cause ?: ex
        } else {
            ex
        }
    }
}
