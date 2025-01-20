package com.wutsi.koki.portal.file.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.file.service.FileService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder

@Deprecated("")
@Controller
class ListFileWidgetController(private val service: FileService) {
    @GetMapping("/files/widgets/list")
    fun list(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val url = service.uploadUrl(
            ownerId = ownerId,
            ownerType = ownerType,
            workflowInstanceId = workflowInstanceId
        )

        var uploadUrl = "/files/upload?upload-url=" + URLEncoder.encode(url, "utf-8")
        model.addAttribute("uploadUrl", uploadUrl)
        model.addAttribute("testMode", testMode)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        more(workflowInstanceId, ownerId, ownerType, limit, offset, model)
        return "files/widgets/list"
    }

    @GetMapping("/files/widgets/list/more")
    fun more(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val files = service.files(
            workflowInstanceIds = workflowInstanceId?.let { id -> listOf(id) } ?: emptyList(),
            ownerId = ownerId,
            ownerType = ownerType,
        )
        if (files.isNotEmpty()) {
            model.addAttribute("files", files)

            if (files.size >= limit) {
                val nextOffset = offset + limit
                val url = listOf(
                    "/files/widgets/list/more?limit=$limit&offset=$nextOffset",
                    ownerId?.let { "owner-id=$ownerId" },
                    ownerType?.let { "owner-type=$ownerType" },
                    workflowInstanceId?.let { "workflow-instance-id=$workflowInstanceId" },
                ).filterNotNull()
                    .joinToString(separator = "&")
                model.addAttribute("moreUrl", url)
            }
        }

        return "files/widgets/list-more"
    }
}
