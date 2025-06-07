package com.wutsi.koki.portal.tenant.page.settings.type

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.TypeService
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/settings/tenant/types/import")
@RequiresPermission(permissions = ["tenant:admin"])
class SettingsImportTypeController(private val service: TypeService) : AbstractSettingsTypeController() {
    @GetMapping
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TENANT_SETTINGS_TYPE_IMPORT,
                title = "Import Types",
            )
        )
        model.addAttribute("objectTypes", getObjectTypes())
        return "tenant/settings/types/import"
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun import(
        @RequestPart file: MultipartFile,
        @RequestParam objectType: ObjectType,
        model: Model
    ): String {
        try {
            val response = service.upload(file, objectType)
            model.addAttribute("response", response)
            return show(model)
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)
            return show(model)
        }
    }
}
