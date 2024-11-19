package com.wutsi.koki.portal.page.workflow

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.model.UserModel
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.UserService
import com.wutsi.koki.portal.service.WorkflowInstanceService
import com.wutsi.koki.portal.service.WorkflowService
import com.wutsi.koki.workflow.dto.Participant
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException
import java.text.SimpleDateFormat
import java.util.Date

@Controller
class StartWorkflowController(
    private val workflowService: WorkflowService,
    private val workflowInstanceService: WorkflowInstanceService,
    private val userService: UserService,
    private val request: HttpServletRequest,
) : AbstractPageController() {
    @GetMapping("/workflows/{id}/start")
    fun start(
        @PathVariable id: Long,
        model: Model
    ): String {
        val workflow = workflowService.workflow(id)
        return start(workflow, model)
    }

    private fun start(workflow: WorkflowModel, model: Model): String {
        model.addAttribute("workflow", workflow)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_START,
                title = workflow.longTitle,
            )
        )

        val userMap = mutableMapOf<Long, List<UserModel>>()
        workflow.roles.forEach { role ->
            val users = userService.search(roleIds = listOf(role.id), limit = 50)
            userMap[role.id] = users
        }
        model.addAttribute("userMap", userMap)

        return "workflows/start"
    }

    @PostMapping("/workflows/{id}/start")
    fun submit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val workflow = workflowService.workflow(id)
        try {
            val form = toStartWorkflowForm(workflow, request)
            val workflowInstanceId = workflowInstanceService.create(form)
            return "redirect:/workflows/{id}/started?instance-id=$workflowInstanceId"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            val error = errorResponse.error.code

            model.addAttribute("error", error)
            return start(workflow, model)
        }
    }

    @GetMapping("/workflows/{id}/started")
    fun create(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val workflow = workflowService.workflow(id)
        model.addAttribute("workflow", workflow)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_STARTED,
                title = workflow.longTitle,
            )
        )
        return "workflows/created"
    }

    private fun toStartWorkflowForm(workflow: WorkflowModel, request: HttpServletRequest): StartWorkflowForm {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return StartWorkflowForm(
            workflowId = workflow.id,
            startAt = fmt.parse(request.getParameter("startAt")),
            dueAt = request.getParameter("dueAt")?.let { date -> fmt.parse(date) },
            approverUserId = request.getParameter("approverId")?.toLong(),
            parameters = workflow.parameters
                .map { param ->
                    param to request.getParameter("parameter_$param")
                }.toMap(),
            participants = workflow.roles
                .mapNotNull { role ->
                    request.getParameter("participant_${role.id}")
                        ?.toLong()
                        ?.let {
                            Participant(roleId = role.id, userId = it)
                        }
                }
        )
    }
}

data class StartWorkflowForm(
    val workflowId: Long = -1,
    val startAt: Date = Date(),
    val dueAt: Date? = null,
    val approverUserId: Long? = null,
    val parameters: Map<String, String>,
    val participants: List<Participant> = emptyList(),
)
