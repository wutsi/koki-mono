package com.wutsi.koki.portal.page.file.widget

import com.wutsi.koki.portal.service.FileService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FileListWidgetController(
    private val fileService: FileService,
) {
    @GetMapping("/files/widgets/list")
    fun show(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        more(workflowInstanceId, limit, offset, model)
        return "files/widgets/list"
    }

    @GetMapping("/files/widgets/list/more")
    fun more(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val files = fileService.files(
            workflowInstanceIds = workflowInstanceId?.let { id -> listOf(id) } ?: emptyList()
        )
        if (files.isNotEmpty()) {
            model.addAttribute("files", files)

            if (files.size >= limit) {
                val nextOffset = offset + limit
                val url = listOf(
                    "/files/widgets/list/more?limit=$limit&offset=$nextOffset",
                    workflowInstanceId?.let { "workflow-instance-id=$workflowInstanceId" },
                ).filterNotNull().joinToString(separator = "&")
                model.addAttribute("moreUrl", url)
            }
        }

        return "files/widgets/list-more"
    }
}
