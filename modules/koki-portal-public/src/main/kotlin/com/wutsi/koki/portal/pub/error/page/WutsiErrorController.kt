package com.wutsi.koki.portal.pub.error.page

import com.wutsi.koki.portal.pub.common.model.PageModel
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.webmvc.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

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
                return handleException(request, ex, model)
            } else {
                val code = request.getAttribute("jakarta.servlet.error.status_code") as Int?
                return handleStatusCode(request, code, model)
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

    private fun handleException(request: HttpServletRequest, ex: Throwable, model: Model): String {
        return if (ex is HttpClientErrorException) {
            handleStatusCode(request, ex.statusCode.value(), model)
        } else {
            handleStatusCode(request, 500, model)
        }
    }

    private fun handleStatusCode(request: HttpServletRequest, code: Int?, model: Model): String {
        if (isAjax(request)) {
            return "error/ajax"
        }

        when (code) {
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

    private fun isAjax(request: HttpServletRequest): Boolean {
        val header = request.getHeader("X-Requested-With")
        return header != null && header.equals("XMLHttpRequest", ignoreCase = true)
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
