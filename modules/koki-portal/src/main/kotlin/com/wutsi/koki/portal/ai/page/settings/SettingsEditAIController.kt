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
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(permissions = ["ai:admin"])
class SettingsEditAIController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/ai/edit")
    fun edit(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.AI_SETTINGS_EDIT,
                title = "AI Settings",
            )
        )

        val configs = service.configurations(keyword = "ai.")
        val form = AISettingsForm(
            model = configs[ConfigurationName.AI_MODEL],
            geminiModel = configs[ConfigurationName.AI_MODEL_GEMINI_MODEL],
            geminiApiKey = configs[ConfigurationName.AI_MODEL_GEMINI_API_KEY],
        )

        return edit(form, model)
    }

    private fun edit(form: AISettingsForm, model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.AI_SETTINGS_EDIT,
                title = "AI Settings",
            )
        )

        model.addAttribute("form", form)
        model.addAttribute("models", listOf("KOKI", "GEMINI"))

        return "ai/settings/edit"
    }

    @PostMapping("/settings/ai/save")
    fun save(@ModelAttribute form: AISettingsForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/settings/ai?_toast=1&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }
}
