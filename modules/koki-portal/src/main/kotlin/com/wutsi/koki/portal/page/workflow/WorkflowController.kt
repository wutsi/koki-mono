package com.wutsi.koki.portal.page.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.sdk.KokiFormData
import com.wutsi.koki.sdk.KokiForms
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FormController(
    private val kokiForms: KokiForms,
    private val kokiFormData: KokiFormData,
) : AbstractPageController() {
    @GetMapping("/forms/{form-id}")
    fun new(
        @PathVariable(name = "form-id") formId: String,
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        model: Model
    ): String {
        val formHtml = kokiForms.html(
            formId = formId,
            formDataId = null,
            workflowInstanceId = workflowInstanceId,
            activityInstanceId = activityInstanceId,
        )
        model.addAttribute("formHtml", formHtml)
        addPageInfo(formId, model)
        return "forms/index"
    }

    @GetMapping("/forms/{form-id}/{form-data-id}")
    fun edit(
        @PathVariable(name = "form-id") formId: String,
        @PathVariable(name = "form-data-id") formDataId: String?,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        model: Model
    ): String {
        val formHtml = kokiForms.html(
            formId = formId,
            formDataId = formDataId,
            workflowInstanceId = null,
            activityInstanceId = activityInstanceId,
        )
        model.addAttribute("formHtml", formHtml)
        addPageInfo(formId, model)
        return "forms/index"
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
            .toMap()
            as Map<String, Any>

        if (formDataId != null) {
            kokiFormData.update(formDataId, activityInstanceId, data)
        } else {
            kokiFormData.submit(formId, workflowInstanceId, activityInstanceId, data)
        }
        return "redirect:/forms/$formId/saved"
    }

    private fun addPageInfo(formId: String, model: Model) {
        val form = kokiForms.get(formId).form
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FORM,
                title = form.title,
            )
        )
    }
}
