package com.wutsi.koki.portal.file.page.image

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/images/tab")
@RequiresPermission(["image"])
class ImageTabController(private val service: FileService) : AbstractPageController() {
    @GetMapping
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        model.addAttribute(
            "uploadUrl",
            service.uploadUrl(
                ownerId = ownerId,
                ownerType = ownerType,
                type = FileType.IMAGE,
            )
        )
        model.addAttribute("testMode", testMode)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        more(ownerId, ownerType, readOnly, limit, offset, model)
        return "files/images/tab/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val files = service.files(
            ownerId = ownerId,
            ownerType = ownerType,
            type = FileType.IMAGE,
            limit = limit,
            offset = offset
        )
        model.addAttribute("readOnly", readOnly ?: false)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        if (files.isNotEmpty()) {
            model.addAttribute("images", files)

            if (files.size >= limit) {
                val nextOffset = offset + limit
                var url =
                    "/images/tab/more.html?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
                if (readOnly != null) {
                    url = "$url&read-only=$readOnly"
                }
                model.addAttribute("moreUrl", url)
            }
        }
        return "files/images/tab/more"
    }

    @GetMapping("/delete")
    @ResponseBody
    @RequiresPermission(["image:manage"])
    fun delete(@RequestParam id: Long): Map<String, Any> {
        try {
            service.delete(id)
            return mapOf(
                "success" to true,
            )
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            return mapOf(
                "success" to false,
                "error" to errorResponse.error.code
            )
        }
    }
}
