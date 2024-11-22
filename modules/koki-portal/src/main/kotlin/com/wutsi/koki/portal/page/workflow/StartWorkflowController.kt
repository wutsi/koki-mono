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
import org.springframework.web.bind.annotation.RequestParam
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
        return start(workflow, StartWorkflowForm(), model)
    }

    @PostMapping("/workflows/{id}/start")
    fun submit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val workflow = workflowService.workflow(id)
        val form = toStartWorkflowForm(workflow, request)
        try {
            val workflowInstanceId = workflowInstanceService.create(form)
            return "redirect:/workflows/{id}/started?workflow-instance-id=$workflowInstanceId"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            val error = errorResponse.error.code

            model.addAttribute("error", error)
            return start(workflow, form, model)
        }
    }

    @GetMapping("/workflows/{id}/started")
    fun started(
        @PathVariable id: String,
        @RequestParam(name = "workflow-instance-id") workflowInstanceId: String,
        model: Model,
    ): String {
        val workflowInstance = workflowInstanceService.workflow(workflowInstanceId)
        model.addAttribute("workflowInstance", workflowInstance)
        model.addAttribute("workflow", workflowInstance.workflow)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_STARTED,
                title = workflowInstance.workflow.longTitle,
            )
        )
        return "workflows/started"
    }

    private fun start(workflow: WorkflowModel, form: StartWorkflowForm, model: Model): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        model.addAttribute("today", fmt.format(Date()))
        model.addAttribute("form", form)
        model.addAttribute("workflow", workflow)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_START,
                title = workflow.longTitle,
            )
        )

        val userMap = mutableMapOf<Long, List<UserModel>>()
        val roleIds = mutableListOf<Long>()
        roleIds.addAll(workflow.roles.map { role -> role.id })
        if (workflow.approverRole != null) {
            roleIds.add(workflow.approverRole.id)
        }
        roleIds.toSet().forEach { roleId ->
            val users = userService.users(roleIds = listOf(roleId), limit = 50)
            userMap[roleId] = users
        }
        model.addAttribute("userMap", userMap)

        return "workflows/start"
    }

    private fun toStartWorkflowForm(workflow: WorkflowModel, request: HttpServletRequest): StartWorkflowForm {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return StartWorkflowForm(
            workflowId = workflow.id,
            approverUserId = request.getParameter("approverId")?.toLong(),
            startNow = request.getParameter("startNow") == "1",

            startAt = if (request.getParameter("startNow") == "1") {
                Date()
            } else {
                fmt.parse(request.getParameter("startAt"))
            },

            dueAt = request.getParameter("dueAt")
                ?.ifEmpty { null }
                ?.let { date -> fmt.parse(date) },

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
    val startNow: Boolean = true,
    val startAt: Date = Date(),
    val dueAt: Date? = null,
    val approverUserId: Long? = null,
    val parameters: Map<String, String> = emptyMap(),
    val participants: List<Participant> = emptyList(),
)
