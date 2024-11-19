package com.wutsi.koki.portal.page.workflow.widget

import com.wutsi.koki.portal.service.WorkflowInstanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MyActivityWidgetController(
    private val workflowInstanceService: WorkflowInstanceService
) {
    @GetMapping("/workflows/widgets/my-activities")
    fun show(model: Model): String {
        val activityInstances = workflowInstanceService.myActivities()
        if (activityInstances.isNotEmpty()) {
            model.addAttribute("activityInstances", activityInstances)
        }
        return "workflows/widgets/my-activities"
    }
}
