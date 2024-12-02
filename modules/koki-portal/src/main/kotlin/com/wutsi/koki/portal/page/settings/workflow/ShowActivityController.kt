package com.wutsi.koki.portal.page.workflow.instance

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ShowActivityController(
    private val workflowService: WorkflowService,
) : AbstractPageController() {
    @GetMapping("/settings/workflows/{workflow-id}/activities/{activity-id}")
    fun show(
        @PathVariable(name = "workflow-id") workflowId: Long,
        @PathVariable(name = "activity-id") activityId: Long,
        model: Model
    ): String {
        val workflow = workflowService.workflow(workflowId)
        model.addAttribute("workflow", workflow)

        val activity = workflow.activities.find { activity -> activity.id == activityId }!!
        model.addAttribute("activity", activity)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_WORKFLOW_ACTIVITY,
                title = activity.longTitle
            )
        )
        return "settings/workflows/activity"
    }
}
