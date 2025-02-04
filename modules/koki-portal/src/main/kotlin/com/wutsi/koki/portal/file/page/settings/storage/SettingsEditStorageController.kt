package com.wutsi.koki.portal.file.page.settings

import com.amazonaws.regions.Regions
import com.wutsi.koki.portal.file.form.StorageForm
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["file:admin"])
class SettingsEditStorageController(private val service: ConfigurationService) : AbstractPageController() {
    @GetMapping("/settings/files/storage/edit")
    fun edit(
        model: Model,
    ): String {
        val config = service.configurations(keyword = "storage.")
        val form = if (config.isNotEmpty()) {
            StorageForm(
                type = config[ConfigurationName.STORAGE_TYPE] ?: "KOKI",
                s3Bucket = config[ConfigurationName.STORAGE_S3_BUCKET] ?: "",
                s3Region = config[ConfigurationName.STORAGE_S3_REGION] ?: "",
                s3AccessKey = config[ConfigurationName.STORAGE_S3_ACCESS_KEY] ?: "",
                s3SecretKey = config[ConfigurationName.STORAGE_S3_SECRET_KEY] ?: "",
            )
        } else {
            StorageForm(type = "KOKI")
        }
        return edit(form, model)
    }

    private fun edit(form: StorageForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FILE_SETTINGS_STORAGE_EDIT,
                title = "File Settings"
            )
        )

        model.addAttribute("s3Regions", Regions.entries.map { region -> region.getName() }.sorted())
        model.addAttribute("types", listOf("KOKI", "S3"))
        return "files/settings/storage/edit"
    }

    @PostMapping("/settings/files/storage/save")
    fun save(@ModelAttribute form: StorageForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/settings/files/storage?_toast=1&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }
}
