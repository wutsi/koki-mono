package com.wutsi.koki.portal.page.workflow.widget

import com.wutsi.koki.portal.rest.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WorkflowListWidgetController(
    private val workflowService: WorkflowService
) {
    @GetMapping("/workflows/widgets/list")
    fun show(model: Model): String {
        val workflows = workflowService.workflows()
        if (workflows.isNotEmpty()) {
            model.addAttribute("workflows", workflows)
        }
        return "workflows/widgets/list"
    }
}
