package com.wutsi.koki.portal.page.workflow

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.FileService
import com.wutsi.koki.portal.service.WorkflowInstanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ShowWorkflowInstanceController(
    private val service: WorkflowInstanceService,
    private val fileService: FileService,
) : AbstractPageController() {
    @GetMapping("/workflows/instances/{id}")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val workflowInstance = service.workflow(id)
        model.addAttribute("workflowInstance", workflowInstance)
        model.addAttribute("activityInstances", workflowInstance.activityInstances.map { it.activity.id to it }.toMap())

        val files = fileService.files(
            workflowInstanceIds = listOf(id)
        )
        if (files.isNotEmpty()) {
            model.addAttribute("files", files)
        }

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_INSTANCE,
                title = workflowInstance.workflow.longTitle
            )
        )
        return "workflows/instances/show"
    }
}
