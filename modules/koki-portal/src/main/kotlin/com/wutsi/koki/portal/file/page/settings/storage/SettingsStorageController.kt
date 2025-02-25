package com.wutsi.koki.portal.file.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.file.form.StorageForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["file:admin"])
class SettingsStorageController(private val service: ConfigurationService) : AbstractPageController() {
    @GetMapping("/settings/files/storage")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model,
    ): String {
        val config = service.configurations(keyword = "storage.")
        val form = if (config.isNotEmpty()) {
            StorageForm(
                type = config[ConfigurationName.STORAGE_TYPE] ?: "KOKI",
                s3Bucket = config[ConfigurationName.STORAGE_S3_BUCKET] ?: "",
                s3Region = config[ConfigurationName.STORAGE_S3_REGION] ?: "",
                s3AccessKey = "*****",
                s3SecretKey = "*****",
            )
        } else {
            StorageForm(type = "KOKI")
        }
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FILE_SETTINGS_STORAGE,
                title = "File Settings"
            )
        )
        loadToast(referer, toast, timestamp, model)
        return "files/settings/storage/show"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/settings/files/storage/edit"))) {
            model.addAttribute("toast", "Saved")
        }
    }
}
