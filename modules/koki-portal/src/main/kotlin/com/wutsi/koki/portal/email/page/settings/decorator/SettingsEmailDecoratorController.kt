package com.wutsi.koki.portal.email.page.settings.decorator

import com.wutsi.koki.portal.email.model.EmailDecoratorForm
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["email:admin"])
class SettingsEmailDecoratorController(
    private val service: ConfigurationService,
) : AbstractPageController() {
    @GetMapping("/settings/email/decorator")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
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
        loadToast(referer, toast, timestamp, model)
        return "emails/settings/decorator/show"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/settings/email/decorator/edit"))) {
            model.addAttribute("toast", "Saved")
        }
    }
}
