package com.wutsi.koki.portal.page.workflow

import com.fasterxml.jackson.core.JacksonException
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.WorkflowService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class CreateWorkflowController(service: WorkflowService) : AbstractSaveWorkflowController(service) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CreateWorkflowController::class.java)
    }

    @GetMapping("/workflows/create")
    fun create(model: Model): String {
        return create(CreateWorkflowForm(), model)
    }

    @PostMapping("/workflows/create")
    fun submit(
        @ModelAttribute form: CreateWorkflowForm,
        model: Model
    ): String {
        try {
            val id = service.create(form)
            return "redirect:/workflows/$id/created"
        } catch (ex: Exception) {
            LOGGER.error("Failed", ex)

            if (ex is HttpClientErrorException) {
                loadErrors(ex, model)
            } else if (ex is JacksonException) {
                model.addAttribute("error", "The JSON is not valid")
            }
            model.addAttribute("form", form)
            return create(form, model)
        }
    }

    @GetMapping("/workflows/{id}/created")
    fun create(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val workflow = service.workflow(id)
        model.addAttribute("workflow", workflow)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_CREATED,
                title = workflow.longTitle
            )
        )
        return "workflows/created"
    }

    private fun create(form: CreateWorkflowForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_CREATE,
                title = "Workflow",
            )
        )
        return "workflows/create"
    }
}

data class CreateWorkflowForm(val json: String = "")
