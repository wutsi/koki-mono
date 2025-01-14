package com.wutsi.koki.portal.file.page

import com.wutsi.koki.portal.file.service.FileService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping
class UploadFileController(private val service: FileService) {
    @GetMapping("/files/upload")
    fun download(
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: String? = null,
        model: Model,
    ): String {
        val uploadUrl = service.uploadUrl(
            ownerId = ownerId,
            ownerType = ownerType,
        )
        model.addAttribute("uploadUrl", uploadUrl)
        return "files/upload"
    }
}
