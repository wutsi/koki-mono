package com.wutsi.koki.portal.page.form.widget

import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.portal.service.WorkflowInstanceService
import com.wutsi.koki.portal.service.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FormsWidgetController(
    private val workflowService: WorkflowService,
    private val workflowInstanceService: WorkflowInstanceService,
) {
    @GetMapping("/forms/widgets/list")
    fun show(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "workflow-id") workflowId: Long? = null,
        model: Model
    ): String {
        val forms = if (workflowId != null) {
            findByWorkflow(workflowId)
        } else if (workflowInstanceId != null) {
            findByWorkflowInstance(workflowInstanceId)
        } else {
            emptyList()
        }
        if (forms.isNotEmpty()) {
            model.addAttribute("forms", forms)
        }

        return "forms/widgets/list"
    }

    private fun findByWorkflowInstance(workflowInstanceId: String): List<FormModel> {
        val workflowInstance = workflowInstanceService.workflow(workflowInstanceId)
        return workflowInstance.activityInstances
            .mapNotNull { activityInstance -> activityInstance.activity.form }
            .distinctBy { form -> form.id }
    }

    private fun findByWorkflow(workflowId: Long): List<FormModel> {
        val workflow = workflowService.workflow(workflowId)
        return workflow.activities
            .mapNotNull { activity -> activity.form }
            .distinctBy { form -> form.id }
    }
}
