package com.wutsi.koki.portal.page.workflow.widget

import com.wutsi.koki.portal.service.CurrentUserHolder
import com.wutsi.koki.portal.service.WorkflowInstanceService
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MyTasksWidgetController(
    private val workflowInstanceService: WorkflowInstanceService,
    private val currentUserHolder: CurrentUserHolder,
) {
    @GetMapping("/workflows/widgets/my-tasks")
    fun show(model: Model): String {
        val me = currentUserHolder.get()
        if (me != null) {
            val activityInstances = workflowInstanceService.activities(
                status = WorkflowStatus.RUNNING,
                assigneeIds = listOf(me.id)
            )
            if (activityInstances.isNotEmpty()) {
                model.addAttribute("activityInstances", activityInstances)
            }
        }
        return "workflows/widgets/my-tasks"
    }
}
