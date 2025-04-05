package com.wutsi.koki.portal.translation.page.settings

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
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(permissions = ["translation:admin"])
class SettingsTranslationController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/translations")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model
    ): String {
        model.addAttribute(
            "page", PageModel(
                name = PageName.TRANSLATION_SETTINGS,
                title = "Translation Settings",
            )
        )

        val configs = service.configurations(
            names = listOf(
                ConfigurationName.TRANSLATION_PROVIDER,
                ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION
            )
        )
        model.addAttribute(
            "form",
            TranslationSettingsForm(
                provider = configs[ConfigurationName.TRANSLATION_PROVIDER],
                awsRegion = configs[ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION],
            )
        )

        loadToast(referer, toast, timestamp, model)
        return "translations/settings/show"
    }

    private fun loadToast(
        referer: String?, toast: Long?, timestamp: Long?, model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/settings/translations/edit"))) {
            model.addAttribute("toast", "Saved")
        }
    }
}
