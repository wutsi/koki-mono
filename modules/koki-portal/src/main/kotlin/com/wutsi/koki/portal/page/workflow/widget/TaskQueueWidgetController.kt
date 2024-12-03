package com.wutsi.koki.portal.page.workflow.widget

import com.wutsi.koki.portal.service.CurrentUserHolder
import com.wutsi.koki.portal.service.WorkflowInstanceService
import com.wutsi.koki.portal.service.WorkflowService
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class TaskQueueWidgetController(
    private val workflowService: WorkflowService,
    private val workflowInstanceService: WorkflowInstanceService,
    private val currentUserHolder: CurrentUserHolder,
) {
    @GetMapping("/workflows/widgets/task-queue")
    fun show(model: Model): String {
        val me = currentUserHolder.get()
        if (me != null) {
            val roleIds = me.roles.map { role -> role.id }
            if (roleIds.isNotEmpty()) {
                // All unassigned activity instances
                val activityInstances = workflowInstanceService.activities(
                    assigneeIds = listOf(-1),
                    status = WorkflowStatus.RUNNING,
                    limit = 50,
                )
                if (activityInstances.isNotEmpty()) {
                    // All activities associated with my role
                    val activityIds = workflowService.activities(
                        ids = activityInstances.map { activityInstance -> activityInstance.activity.id },
                        roleIds = roleIds,
                    ).map { activity -> activity.id }

                    // Filter unassigned activity instances associated with my role
                    val instances = activityInstances.filter { activityInstance ->
                        activityIds.contains(activityInstance.activity.id)
                    }
                    if (instances.isNotEmpty()) {
                        model.addAttribute("activityInstances", instances)
                    }
                }
            }
        }
        return "workflows/widgets/task-queue"
    }

    @GetMapping("/workflows/widgets/task-queue/assign")
    fun assign(@RequestParam(name = "activity-instance-id") activityInstanceId: String): String {
        val userId = currentUserHolder.id()
        if (userId != null) {
            workflowInstanceService.assignee(activityInstanceId, userId)
        }
        return "redirect:/"
    }
}
