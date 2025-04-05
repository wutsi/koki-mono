package com.wutsi.koki.portal.translation.page.settings

import com.amazonaws.regions.Regions
import com.wutsi.koki.platform.translation.TranslationProvider
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.portal.translation.form.TranslationSettingsForm
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(permissions = ["translation:admin"])
class SettingsEditTranslationController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/translations/edit")
    fun edit(model: Model): String {
        val configs = service.configurations(
            names = listOf(
                ConfigurationName.AI_MODEL,
                ConfigurationName.TRANSLATION_PROVIDER,
                ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION,
                ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY,
                ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY,
            )
        )
        val form = TranslationSettingsForm(
            aiModel = configs[ConfigurationName.AI_MODEL],
            provider = configs[ConfigurationName.TRANSLATION_PROVIDER] ?: "",
            awsRegion = configs[ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION],
            awsSecretKey = configs[ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY],
            awsAccessKey = configs[ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY],
        )
        return edit(form, model)
    }

    private fun edit(form: TranslationSettingsForm, model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TRANSLATION_SETTINGS_EDIT,
                title = "Translation Settings",
            )
        )

        model.addAttribute("form", form)
        model.addAttribute(
            "providers",
            listOf(
                TranslationProvider.AI,
                TranslationProvider.AWS,
            )
        )

        model.addAttribute("awsRegions", Regions.entries.map { region -> region.getName() }.sorted())
        return "translations/settings/edit"
    }

    @PostMapping("/settings/translations/save")
    fun save(@ModelAttribute form: TranslationSettingsForm, model: Model): String {
        try {
            service.save(
                configs = when (form.provider) {
                    TranslationProvider.AWS.name -> mapOf(
                        ConfigurationName.TRANSLATION_PROVIDER to form.provider,
                        ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION to (form.awsRegion ?: ""),
                        ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY to (form.awsAccessKey ?: ""),
                        ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY to (form.awsSecretKey ?: ""),
                    )

                    else -> mapOf(
                        ConfigurationName.TRANSLATION_PROVIDER to form.provider,
                    )
                }
            )
            return "redirect:/settings/translations?_toast=1&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }
}
