package com.wutsi.koki.portal.tax.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.form.TaxAIAgentForm
import com.wutsi.koki.portal.tax.form.TaxNotificationForm
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["tax:admin"])
class SettingsTaxController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/taxes")
    fun show(model: Model): String {
        val configs = service.configurations(
            names = listOf(
                ConfigurationName.AI_PROVIDER,
                ConfigurationName.TAX_AI_AGENT_ENABLED,
                ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED,
                ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT,
                ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED,
                ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT,
                ConfigurationName.TAX_EMAIL_DONE_ENABLED,
                ConfigurationName.TAX_EMAIL_DONE_SUBJECT,
            )
        )
        model.addAttribute(
            "aiAgent",
            TaxAIAgentForm(
                aiProvider = configs[ConfigurationName.AI_PROVIDER],
                enabled = configs[ConfigurationName.TAX_AI_AGENT_ENABLED] != null,
            )
        )
        model.addAttribute(
            "assigneeNotification",
            TaxNotificationForm(
                enabled = configs[ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED] != null,
                subject = configs[ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT],
            )
        )
        model.addAttribute(
            "documentNotification",
            TaxNotificationForm(
                enabled = configs[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED] != null,
                subject = configs[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT],
            )
        )
        model.addAttribute(
            "doneNotification",
            TaxNotificationForm(
                enabled = configs[ConfigurationName.TAX_EMAIL_DONE_ENABLED] != null,
                subject = configs[ConfigurationName.TAX_EMAIL_DONE_SUBJECT],
            )
        )

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TAX_SETTINGS,
                title = "Taxes",
            )
        )

        return "taxes/settings/show"
    }

    @GetMapping("/settings/taxes/agent/enable")
    fun enable(
        @RequestParam status: Boolean,
    ): String {
        service.enable(ConfigurationName.TAX_AI_AGENT_ENABLED, status)
        return "redirect:/settings/taxes"
    }
}
