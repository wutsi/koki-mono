package com.wutsi.koki.portal.page.log.widget

import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.service.LogService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class LogWidgetController(
    private val logService: LogService
) : AbstractPageController() {
    @GetMapping("/workflows/widgets/logs")
    fun show(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        more(workflowInstanceId, activityInstanceId, limit, offset, model)
        return "workflows/widgets/logs"
    }

    @GetMapping("/workflows/widgets/logs/more")
    fun more(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val logs = logService.logs(
            workflowInstanceId = workflowInstanceId,
            activityInstanceId = activityInstanceId,
            limit = limit,
            offset = offset
        )
        if (logs.isNotEmpty()) {
            model.addAttribute("logs", logs)

            if (logs.size >= limit) {
                val nextOffset = offset + limit
                val url = listOf(
                    "/workflows/widgets/logs/more?limit=$limit&offset=$nextOffset",
                    workflowInstanceId?.let { "workflow-instance-id=$workflowInstanceId" },
                    activityInstanceId?.let { "activity-instance-id=$activityInstanceId" }
                ).filterNotNull().joinToString(separator = "&")
                model.addAttribute("moreUrl", url)
            }
        }
        return "workflows/widgets/logs-more"
    }
}
