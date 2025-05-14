package com.wutsi.koki.portal.file.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping
@RequiresPermission(["file:manage"])
class UploadFileController(private val service: FileService) {
    @GetMapping("/files/upload")
    fun upload(
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        model: Model,
    ): String {
        val uploadUrl = service.uploadUrl(
            ownerId = ownerId,
            ownerType = ownerType,
            type = FileType.FILE,
        )
        model.addAttribute("uploadUrl", uploadUrl)
        return "files/upload"
    }
}
