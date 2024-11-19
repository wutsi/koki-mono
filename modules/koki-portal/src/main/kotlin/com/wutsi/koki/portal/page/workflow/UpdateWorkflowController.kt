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
class UpdateWorkflowController(service: WorkflowService) : AbstractSaveWorkflowController(service) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UpdateWorkflowController::class.java)
    }

    @GetMapping("/workflows/{id}/update")
    fun update(
        @PathVariable id: Long,
        model: Model
    ): String {
        val form = UpdateFormWorkflow(
            json = service.json(id)
        )
        return edit(id, form, model)
    }

    @PostMapping("/workflows/{id}/update")
    fun submit(
        @PathVariable id: Long,
        @ModelAttribute form: UpdateFormWorkflow,
        model: Model
    ): String {
        try {
            service.update(id, form)
            return "redirect:/workflows/$id/updated"
        } catch (ex: Exception) {
            LOGGER.error("Failed", ex)

            if (ex is HttpClientErrorException) {
                loadErrors(ex, model)
            } else if (ex is JacksonException) {
                model.addAttribute("error", "The JSON is not valid")
            }
            model.addAttribute("form", form)
            return edit(id, form, model)
        }
    }

    @GetMapping("/workflows/{id}/updated")
    fun create(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val workflow = service.workflow(id)
        model.addAttribute("workflow", workflow)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_UPDATED,
                title = workflow.longTitle
            )
        )
        return "workflows/updated"
    }

    fun edit(
        id: Long,
        form: UpdateFormWorkflow,
        model: Model
    ): String {
        val workflow = service.workflow(id)

        model.addAttribute("workflow", workflow)
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.WORKFLOW_UPDATE,
                title = workflow.longTitle,
            )
        )
        return "workflows/update"
    }
}

data class UpdateFormWorkflow(val json: String = "")
