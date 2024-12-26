package com.wutsi.koki.portal.page.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.FormService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FormController(private val service: FormService) : AbstractPageController() {
    @GetMapping("/forms/{form-id}")
    fun show(
        @PathVariable(name = "form-id") formId: String,
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean = false,
        @RequestParam(required = false, name = "preview") preview: Boolean = false,
        model: Model
    ): String {
        val form = service.form(formId)
        val formHtml = service.html(
            formId = formId,
            formDataId = null,
            workflowInstanceId = workflowInstanceId,
            activityInstanceId = activityInstanceId,
            readOnly = readOnly,
            preview = preview,
        )
        model.addAttribute("formHtml", formHtml)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FORM,
                title = form.longTitle,
            )
        )
        return "forms/show"
    }

    @GetMapping("/forms/{form-id}/{form-data-id}")
    fun edit(
        @PathVariable(name = "form-id") formId: String,
        @PathVariable(name = "form-data-id") formDataId: String?,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        model: Model
    ): String {
        val form = service.form(formId)
        val formHtml = service.html(
            formId = formId,
            formDataId = formDataId,
            workflowInstanceId = null,
            activityInstanceId = activityInstanceId,
        )
        model.addAttribute("formHtml", formHtml)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FORM,
                title = form.longTitle,
            )
        )

        return "forms/show"
    }

    @PostMapping("/forms/{form-id}")
    fun submit(
        @PathVariable(name = "form-id") formId: String,
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        request: HttpServletRequest
    ): String {
        return submit(formId, null, workflowInstanceId, activityInstanceId, request)
    }

    @PostMapping("/forms/{form-id}/{form-data-id}")
    fun submit(
        @PathVariable(name = "form-id") formId: String,
        @PathVariable(name = "form-data-id") formDataId: String?,
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        request: HttpServletRequest
    ): String {
        val data = request.parameterMap
            .filter { entry -> entry.key != "workflow-instance-id" && entry.key != "activity-instance-id" }
            .map { entry ->
                if (entry.value.size == 1) {
                    entry.key to entry.value[0]
                } else {
                    entry.key to entry.value
                }
            }
            .toMap() as Map<String, Any>
        if (formDataId != null) {
            service.submit(formDataId, activityInstanceId, data)
        } else {
            service.submit(formId, workflowInstanceId, activityInstanceId, data)
        }

        if (activityInstanceId == null) {
            return "redirect:/forms/$formId/sumitted"
        } else {
            return "redirect:/tasks/$activityInstanceId/completed"
        }
    }

    @GetMapping("/forms/{id}/submitted")
    fun submitted(
        @PathVariable id: String,
        model: Model
    ): String {
        val form = service.form(id)

        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FORM_SUBMITTED,
                title = form.longTitle,
            )
        )
        return "forms/submitted"
    }
}
