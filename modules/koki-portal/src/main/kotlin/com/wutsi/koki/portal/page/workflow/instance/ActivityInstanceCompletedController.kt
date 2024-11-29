package com.wutsi.koki.portal.page.workflow

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.WorkflowInstanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ActivityInstanceCompletedController(
    private val workflowInstanceService: WorkflowInstanceService,
) : AbstractPageController() {
    @GetMapping("/workflows/instances/activities/{id}/completed")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val activityInstance = workflowInstanceService.activity(id)
        model.addAttribute("activityInstance", activityInstance)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACTIVITY_INSTANCE_COMPLETED,
                title = activityInstance.activity.longTitle
            )
        )
        return "workflows/instances/activity-completed"
    }
}
