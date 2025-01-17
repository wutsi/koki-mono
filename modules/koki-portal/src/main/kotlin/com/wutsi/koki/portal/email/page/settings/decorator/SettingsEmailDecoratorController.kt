package com.wutsi.koki.portal.email.page.settings.decorator

import com.wutsi.koki.portal.email.model.EmailDecoratorForm
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SettingsEmailDecoratorController(
    private val service: ConfigurationService,
) : AbstractPageController() {
    @GetMapping("/settings/email/decorator")
    fun show(model: Model): String {
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
        return "emails/settings/decorator/show"
    }
}
