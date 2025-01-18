package com.wutsi.koki.portal.page.workflow.instance

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.FormService
import com.wutsi.koki.portal.service.WorkflowInstanceService
import com.wutsi.koki.portal.user.service.CurrentUserHolder
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class TaskController(
    private val service: WorkflowInstanceService,
    private val currentUser: CurrentUserHolder,
    private val formService: FormService,
) : AbstractPageController() {
    @GetMapping("/tasks/{id}")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val task = service.activity(id)
        model.addAttribute("task", task)

        val user = currentUser.get()
        val canComplete = (user?.id == task.assignee?.id) && (task.status == WorkflowStatus.RUNNING)
        model.addAttribute("canComplete", canComplete)

        if (task.activity.type == ActivityType.USER) {
            if (task.activity.form != null) {
                val html = formService.html(
                    formId = task.activity.form.id,
                    workflowInstanceId = task.workflowInstance.id,
                    activityInstanceId = task.id,
                    preview = false,
                    readOnly = !canComplete,
                )
                model.addAttribute("formHtml", html)
            }
        }

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.TASK,
                title = task.activity.title,
            )
        )
        return "workflows/task"
    }

    @PostMapping("/tasks/{id}/complete")
    fun complete(
        @PathVariable id: String,
        model: Model
    ): String {
        try {
            service.complete(id, emptyMap())
            return completed(id, model)
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id, model)
        }
    }

    @GetMapping("/tasks/{id}/completed")
    fun completed(
        @PathVariable id: String,
        model: Model
    ): String {
        try {
            val task = service.activity(id)
            model.addAttribute("task", task)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.TASK_COMPLETED,
                    title = task.activity.title
                )
            )
            return "workflows/task-completed"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id, model)
        }
    }
}
