package com.wutsi.koki.portal.file.page.settings

import com.wutsi.koki.portal.email.model.SMTPForm
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
@RequiresPermission(["file:admin"])
class SettingsFileController(private val service: ConfigurationService): AbstractPageController(){
    @GetMapping("/settings/files")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model,
    ): String{
        val config = service.configurations(keyword = "storage.")
        if (config.isNotEmpty()) {
            val form = SMTPForm(
                host = config[ConfigurationName.SMTP_HOST] ?: "",
                username = config[ConfigurationName.SMTP_USERNAME] ?: "",
                password = config[ConfigurationName.SMTP_PASSWORD] ?: "",
                fromAddress = config[ConfigurationName.SMTP_FROM_ADDRESS] ?: "",
                fromPersonal = config[ConfigurationName.SMTP_FROM_PERSONAL] ?: "",
            )
            model.addAttribute("form", form)
        }
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FILE_SETTINGS,
                title = "File Settings"
            )
        )
        loadToast(referer, toast, timestamp, model)
        return "files/settings/show"

    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/settings/files/edit"))) {
            model.addAttribute("toast", "Saved")
        }
    }
}
