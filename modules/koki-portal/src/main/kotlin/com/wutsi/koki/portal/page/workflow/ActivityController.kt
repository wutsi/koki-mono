package com.wutsi.koki.portal.page.workflow.instance

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.WorkflowInstanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.client.HttpClientErrorException

@Controller
class ActivityController(
    private val workflowInstanceService: WorkflowInstanceService,
) : AbstractPageController() {
    @GetMapping("/workflows/activities/{id}")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val activityInstance = workflowInstanceService.activity(id)
        model.addAttribute("activityInstance", activityInstance)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_ACTIVITY,
                title = activityInstance.activity.longTitle
            )
        )
        return "workflows/activity"
    }

    @GetMapping("/workflows/activities/{id}/complete")
    fun complete(
        @PathVariable id: String,
        model: Model
    ): String {
        try {
            workflowInstanceService.completeActivity(id, emptyMap())

            val activityInstance = workflowInstanceService.activity(id)
            model.addAttribute("activityInstance", activityInstance)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.WORKFLOW_ACTIVITY_COMPLETED,
                    title = activityInstance.activity.longTitle
                )
            )
            return "workflows/activity-completed"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id, model)
        }
    }
}
