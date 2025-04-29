package com.wutsi.koki.portal.client.file.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.common.page.PageName
import com.wutsi.koki.portal.client.file.service.FileService
import com.wutsi.koki.portal.client.security.RequiresModule
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/files/tab")
@RequiresModule(name = "file")
class FileTabController(
    private val service: FileService
) : AbstractPageController() {
    @GetMapping
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        model: Model
    ): String {
        model.addAttribute("testMode", testMode)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("uploadUrl", "/files/tab/upload?owner-id=$ownerId&owner-type=$ownerType")
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FILE_TAB,
                title = "Files",
            )
        )
        more(ownerId, ownerType, readOnly = readOnly, model = model)
        return "files/tab/list"
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
            limit = limit,
            offset = offset,
        )
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("readOnly", readOnly)
        if (files.isNotEmpty()) {
            model.addAttribute("files", files)

            if (files.size >= limit) {
                val nextOffset = offset + limit
                var url = "/files/tab/more?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
                model.addAttribute("moreUrl", url)
            }
        }

        return "files/tab/more"
    }

    @GetMapping("/delete")
    fun delete(@RequestParam id: Long): String {
        service.delete(id)
        return "/files/tab/deleted"
    }

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam(required = false, name = "owner-id") ownerId: Long,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType,
        @RequestPart file: MultipartFile,
        model: Model
    ): String {

        val response = service.upload(ownerId, ownerType, file)
        model.addAttribute("response", response)
        return "files/tab/uploaded"
    }
}
