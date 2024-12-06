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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
class EditWorkflowController(service: WorkflowService) : AbstractSaveWorkflowController(service) {
    @GetMapping("/settings/workflows/{id}/edit")
    fun update(
        @PathVariable id: Long,
        model: Model
    ): String {
        val form = SaveWorkflowForm(
            json = service.json(id)
        )
        return edit(id, form, model)
    }

    @PostMapping("/settings/workflows/{id}/update")
    fun submit(
        @PathVariable id: Long,
        @ModelAttribute form: SaveWorkflowForm,
        model: Model
    ): String {
        try {
            service.update(id, form)

            val workflow = service.workflow(id)
            model.addAttribute("workflow", workflow)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_WORKFLOW_SAVED,
                    title = workflow.longTitle
                )
            )
            return "settings/workflows/saved"
        } catch (ex: Exception) {
            if (ex is HttpClientErrorException) {
                loadErrors(ex, model)
            } else if (ex is JacksonException) {
                model.addAttribute("error", "The JSON is not valid")
            } else {
                throw ex
            }
            model.addAttribute("form", form)
            return edit(id, form, model)
        }
    }

    fun edit(
        id: Long,
        form: SaveWorkflowForm,
        model: Model
    ): String {
        val workflow = service.workflow(id)

        model.addAttribute("workflow", workflow)
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_WORKFLOW_EDIT,
                title = workflow.longTitle,
            )
        )
        return "settings/workflows/edit"
    }
}
