package com.wutsi.koki.portal.page.workflow.widget

import com.wutsi.koki.portal.service.WorkflowInstanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class WorkflowInstanceWidgetController(
    private val workflowInstanceService: WorkflowInstanceService,
) {
    @GetMapping("/workflows/widgets/workflow-instances")
    fun show(
        @RequestParam(name = "created-by-id", required = false) createById: Long? = null,
        @RequestParam(name = "workflow-id", required = false) workflowId: Long? = null,
        @RequestParam(name = "show-workflow", required = false) showWorkflow: Boolean? = true,
        @RequestParam(required = false) title: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        model.addAttribute("title", title ?: "Workflow Instances")
        more(createById, workflowId, showWorkflow, limit, offset, model)
        return "workflows/widgets/workflow-instances"
    }

    @GetMapping("/workflows/widgets/workflow-instances/more")
    fun more(
        @RequestParam(name = "created-by-id", required = false) createById: Long? = null,
        @RequestParam(name = "workflow-id", required = false) workflowId: Long? = null,
        @RequestParam(name = "show-workflow", required = false) showWorkflow: Boolean? = true,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val workflowInstances = workflowInstanceService.workflows(
            workflowIds = workflowId?.let { listOf(workflowId) } ?: emptyList(),
            createdById = createById,
            limit = limit,
            offset = offset,
        )
        if (workflowInstances.isNotEmpty()) {
            model.addAttribute("workflowInstances", workflowInstances)
            model.addAttribute("showWorkflow", showWorkflow)

            val nextOffset = offset + limit
            val url = listOf(
                "/workflows/widgets/workflow-instances/more?limit=$limit&offset=$nextOffset",
                createById?.let { "created-by-id=$createById" },
                workflowId?.let { "workflow-id=$workflowId" },
            ).filterNotNull().joinToString(separator = "&")
            model.addAttribute("moreUrl", url)
        }
        return "workflows/widgets/workflow-instances-more"
    }
}
