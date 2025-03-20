package com.wutsi.koki.portal.ai.page.settings

import com.wutsi.koki.portal.ai.form.AIForm
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@RequiresPermission(permissions = ["ai:admin"])
class SettingsAIController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/ai")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.AI_SETTINGS,
                title = "AI Settings",
            )

        )

        val configs = service.configurations(keyword = "ai.")
        model.addAttribute(
            "form",
            AIForm(
                type = configs[ConfigurationName.AI_PROVIDER],
                gemimiModel = configs[ConfigurationName.AI_PROVIDER_GEMINI_MODEL],
            )
        )

        return "ai/settings/show"
    }
}
