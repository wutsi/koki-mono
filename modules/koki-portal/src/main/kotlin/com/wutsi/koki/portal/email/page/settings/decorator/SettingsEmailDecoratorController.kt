package com.wutsi.koki.portal.email.page.settings.decorator

import com.wutsi.koki.portal.email.model.EmailDecoratorForm
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SettingsEmailDecoratorController(
    private val service: ConfigurationService,
    private val httpRequest: HttpServletRequest,
) : AbstractPageController() {
    @GetMapping("/settings/email/decorator")
    fun show(
        @RequestParam(required = false) updated: Long? = null,
        model: Model,
    ): String {
        val configs = service.configurations(names = listOf(ConfigurationName.EMAIL_DECORATOR))
        configs[ConfigurationName.EMAIL_DECORATOR]?.let { config ->
            model.addAttribute(
                "form",
                EmailDecoratorForm(content = config)
            )
        }
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.EMAIL_SETTINGS_EMAIL_DECORATOR,
                title = "Email Layout"
            )
        )
        loadUpdatedToast(updated, model)
        return "emails/settings/decorator/show"
    }

    private fun loadUpdatedToast(updated: Long?, model: Model) {
        val referer = httpRequest.getHeader(HttpHeaders.REFERER)
        if (
            updated != null &&
            referer != null &&
            referer.endsWith("/settings/email/decorator/edit")
        ) {
            model.addAttribute("toast", "Saved")
        }
    }
}
