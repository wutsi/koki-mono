package com.wutsi.koki.portal.account.page.settings.type

import com.wutsi.koki.portal.account.service.AccountTypeService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/settings/accounts/types/import")
class SettingsImportAccountTypeController(private val service: AccountTypeService) : AbstractPageController() {
    @GetMapping
    fun show(
        model: Model,
    ): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACCOUNT_SETTINGS_TYPE_IMPORT,
                title = "Import Account Types",
            )
        )
        return "accounts/settings/types/import"
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun import(@RequestPart file: MultipartFile, model: Model): String {
        try {
            val response = service.upload(file)
            model.addAttribute("response", response)
            return show(model)
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)
            return show(model)
        }
    }
}
