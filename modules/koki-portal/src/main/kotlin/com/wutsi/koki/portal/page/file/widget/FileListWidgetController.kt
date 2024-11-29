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
        @RequestParam(required = false) layout: String? = null,
        model: Model
    ): String {
        val files = fileService.files(
            workflowInstanceIds = workflowInstanceId?.let { id -> listOf(id) } ?: emptyList()
        )
        if (files.isNotEmpty()) {
            model.addAttribute("files", files)
            model.addAttribute("layout", layout)
        }

        return "files/widgets/list"
    }
}
