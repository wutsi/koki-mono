package com.wutsi.koki.portal.tax.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tax.form.TaxNotificationForm
import com.wutsi.koki.portal.tax.form.TaxNotificationType
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller()
@RequiresPermission(["tax:admin"])
@RequestMapping("/settings/taxes/notifications")
class SettingsTaxNotificationController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping
    fun edit(
        @RequestParam type: TaxNotificationType,
        model: Model
    ): String {
        val form = loadForm(type)
        return edit(form, model)
    }

    private fun edit(form: TaxNotificationForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TAX_SETTINGS_NOTIFICATION,
                title = "Taxes Notifications"
            )
        )

        return "taxes/settings/notification"
    }

    private fun loadForm(type: TaxNotificationType): TaxNotificationForm {
        val configs = service.configurations(
            names = when (type) {
                TaxNotificationType.assignee -> listOf(
                    ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED,
                    ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT,
                    ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY,
                )

                TaxNotificationType.document -> listOf(
                    ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED,
                    ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT,
                    ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_BODY,
                )

                TaxNotificationType.done -> listOf(
                    ConfigurationName.TAX_EMAIL_DONE_ENABLED,
                    ConfigurationName.TAX_EMAIL_DONE_SUBJECT,
                    ConfigurationName.TAX_EMAIL_DONE_BODY,
                )
            }
        )
        return TaxNotificationForm(
            type = type,
            enabled = when (type) {
                TaxNotificationType.assignee -> configs[ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED] != null
                TaxNotificationType.document -> configs[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED] != null
                TaxNotificationType.done -> configs[ConfigurationName.TAX_EMAIL_DONE_ENABLED] != null
            },
            subject = when (type) {
                TaxNotificationType.assignee -> configs[ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT]
                TaxNotificationType.document -> configs[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT]
                TaxNotificationType.done -> configs[ConfigurationName.TAX_EMAIL_DONE_SUBJECT]
            },
            body = when (type) {
                TaxNotificationType.assignee -> configs[ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY]
                TaxNotificationType.document -> configs[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_BODY]
                TaxNotificationType.done -> configs[ConfigurationName.TAX_EMAIL_DONE_BODY]
            },
        )
    }

    @PostMapping("/save")
    fun save(@ModelAttribute form: TaxNotificationForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/settings/taxes"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }

    @GetMapping("/enable")
    fun enable(
        @RequestParam type: TaxNotificationType,
        @RequestParam status: Boolean,
    ): String {
        if (status) {
            val form = loadForm(type)
            if (form.subject.isNullOrEmpty() || form.body.isNullOrEmpty()) {
                return "redirect:/settings/taxes/notifications?type=$type"
            }
        }

        service.enable(type, status)
        return "redirect:/settings/taxes"
    }
}
