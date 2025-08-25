package com.wutsi.koki.portal.file.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder

@Controller
@RequiresPermission(["file"])
class FileTabController(private val service: FileService) : AbstractPageController() {
    @GetMapping("/files/tab")
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val url = service.uploadUrl(
            ownerId = ownerId,
            ownerType = ownerType,
            type = FileType.FILE,
        )

        var uploadUrl = "/files/upload?upload-url=" + URLEncoder.encode(url, "utf-8")
        model.addAttribute("uploadUrl", uploadUrl)
        model.addAttribute("testMode", testMode)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        more(ownerId, ownerType, readOnly, limit, offset, model)
        return "files/tab"
    }

    @GetMapping("/files/tab/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val files = service.search(
            ownerId = ownerId,
            ownerType = ownerType,
            type = FileType.FILE,
            limit = limit,
            offset = offset
        )
        model.addAttribute("readOnly", readOnly ?: false)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        if (files.isNotEmpty()) {
            model.addAttribute("files", files)

            if (files.size >= limit) {
                val nextOffset = offset + limit
                var url = "/files/tab/more?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
                if (readOnly != null) {
                    url = "$url&read-only=$readOnly"
                }
                model.addAttribute("moreUrl", url)
            }
        }

        return "files/tab-more"
    }
}
