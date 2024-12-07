package com.wutsi.koki.portal.page.log.widget

import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.service.WorkflowInstanceService
import com.wutsi.koki.portal.service.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ImageWidgetController(
    private val workflowInstanceService: WorkflowInstanceService,
    private val workflowService: WorkflowService,
) : AbstractPageController() {
    @GetMapping("/workflows/widgets/image")
    fun show(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "activity-id") workflowId: Long? = null,
        model: Model
    ): String {
        if (workflowInstanceId != null) {
            model.addAttribute("url", workflowInstanceService.imageUrl(workflowInstanceId))
        } else if (workflowId != null){
            model.addAttribute("url", workflowService.imageUrl(workflowId))
        }
        return "workflows/widgets/image"
    }
}
