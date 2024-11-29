package com.wutsi.koki.portal.page.workflow

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
class ShowActivityInstanceController(
    private val workflowInstanceService: WorkflowInstanceService,
) : AbstractPageController() {
    @GetMapping("/workflows/instances/activities/{id}")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val activityInstance = workflowInstanceService.activity(id)
        model.addAttribute("activityInstance", activityInstance)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ACTIVITY_INSTANCE,
                title = activityInstance.activity.longTitle
            )
        )
        return "workflows/instances/activity"
    }

    @GetMapping("/workflows/instances/activities/{id}/complete")
    fun complete(
        @PathVariable id: String,
        model: Model
    ): String {
        try {
            workflowInstanceService.completeActivity(id, emptyMap())
            return "redirect:/workflows/instances/activities/$id/completed"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id, model)
        }
    }
}
