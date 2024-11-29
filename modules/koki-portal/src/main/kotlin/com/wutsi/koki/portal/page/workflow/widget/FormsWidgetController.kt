package com.wutsi.koki.portal.page.workflow.widget

import com.wutsi.koki.portal.service.WorkflowInstanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FormsWidgetController(
    private val workflowInstanceService: WorkflowInstanceService,
) {
    @GetMapping("/workflows/widgets/forms")
    fun show(
        @RequestParam(name = "workflow-instance-id") workflowInstanceId: String,
        model: Model
    ): String {
        val workflowInstance = workflowInstanceService.workflow(workflowInstanceId)
        val forms = workflowInstance.activityInstances.mapNotNull { activityInstance ->
            activityInstance.activity.form
        }.distinctBy { form -> form.id }
        if (forms.isNotEmpty()) {
            model.addAttribute("forms", forms)
        }

        return "workflows/widgets/forms"
    }
}
