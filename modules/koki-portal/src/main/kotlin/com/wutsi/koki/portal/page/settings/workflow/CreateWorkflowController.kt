package com.wutsi.koki.portal.page.workflow

import com.fasterxml.jackson.core.JacksonException
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.page.settings.workflow.AbstractSaveWorkflowController
import com.wutsi.koki.portal.service.WorkflowService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class CreateWorkflowController(service: WorkflowService) : AbstractSaveWorkflowController(service) {
    @GetMapping("/settings/workflows/create")
    fun create(model: Model): String {
        return create(SaveWorkflowForm(), model)
    }

    @PostMapping("/settings/workflows/add-new")
    fun addNew(
        @ModelAttribute form: SaveWorkflowForm,
        model: Model
    ): String {
        try {
            val id = service.create(form)
            val workflow = service.workflow(id)
            model.addAttribute("workflow", workflow)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_WORKFLOW_SAVED,
                    title = workflow.longTitle
                )
            )

            return "/settings/workflows/saved"
        } catch (ex: Exception) {
            if (ex is HttpClientErrorException) {
                loadErrors(ex, model)
            } else if (ex is JacksonException) {
                model.addAttribute("error", "The JSON is not valid")
            } else {
                throw ex
            }
            model.addAttribute("form", form)
            return create(form, model)
        }
    }

    private fun create(form: SaveWorkflowForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_WORKFLOW_CREATE,
                title = "Workflow",
            )
        )
        return "settings/workflows/create"
    }
}
