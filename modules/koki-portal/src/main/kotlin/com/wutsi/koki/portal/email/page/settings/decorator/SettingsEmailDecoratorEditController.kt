package com.wutsi.koki.portal.email.page.settings.decorator

import com.wutsi.koki.portal.email.model.EmailDecoratorForm
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["email:admin"])
class SettingsEmailDecoratorEditController(
    private val service: ConfigurationService,
) : AbstractPageController() {
    @GetMapping("/settings/email/decorator/edit")
    fun edit(model: Model): String {
        val configs = service.configurations(names = listOf(ConfigurationName.EMAIL_DECORATOR))
        val form = EmailDecoratorForm(
            content = configs[ConfigurationName.EMAIL_DECORATOR] ?: ""
        )
        return edit(form, model)
    }

    fun edit(form: EmailDecoratorForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page", PageModel(
                name = PageName.EMAIL_SETTINGS_EMAIL_DECORATOR_EDIT, title = "Email Layout"
            )
        )
        return "emails/settings/decorator/edit"
    }

    @PostMapping("/settings/email/decorator/update")
    fun show(form: EmailDecoratorForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/settings/email/decorator?_toast=1&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }
}
