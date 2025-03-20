package com.wutsi.koki.portal.ai.page.settings

import com.wutsi.koki.portal.ai.form.AISettingsForm
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(permissions = ["ai:admin"])
class SettingsAIController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/ai")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        model.addAttribute(
            "page", PageModel(
                name = PageName.AI_SETTINGS,
                title = "AI Settings",
            )
        )

        val configs = service.configurations(keyword = "ai.")
        model.addAttribute(
            "form", AISettingsForm(
                type = configs[ConfigurationName.AI_PROVIDER],
                geminiModel = configs[ConfigurationName.AI_PROVIDER_GEMINI_MODEL],
            )
        )

        loadToast(referer, toast, timestamp, model)
        return "ai/settings/show"
    }

    private fun loadToast(
        referer: String?, toast: Long?, timestamp: Long?, model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/settings/ai/edit"))) {
            model.addAttribute("toast", "Saved")
        }
    }
}
