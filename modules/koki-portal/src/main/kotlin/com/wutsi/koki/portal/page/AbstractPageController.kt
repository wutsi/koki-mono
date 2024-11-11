package com.wutsi.koki.portal.page.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.rest.KokiForms
import com.wutsi.koki.portal.rest.KokiWorkflowEngine
import com.wutsi.koki.portal.rest.TenantService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
class FormController(
    private val kokiForms: KokiForms,
    private val kokiWorkflowEngine: KokiWorkflowEngine,
    private val tenantService: TenantService,
) {
    @GetMapping("/forms/{id}")
    fun show(
        @PathVariable id: String,
        @RequestParam(required = false, name = "aiid") activityInstanceId: String? = null,
        model: Model
    ): String {
        val formHtml = kokiForms.html(
            formId = id,
            activityInstanceId = activityInstanceId,
            submitUrl = "/forms/$id?aiid=" + (activityInstanceId ?: ""),
            tenantId = tenantService.id()
        )
        model.addAttribute("formHtml", formHtml)

        val form = kokiForms.get(id).form
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FORM,
                title = form.title,
            )
        )
        return "forms/index"
    }

    @PostMapping("/forms/{id}")
    fun submit(
        @PathVariable id: String,
        @RequestParam(required = false, name = "aiid") activityInstanceId: String? = null,
        request: HttpServletRequest
    ): String {
        val data = request.parameterMap
            .filter { entry -> entry.key != "aiid" }
            .map { entry -> entry.key to entry.value[0] }
            .toMap()
            as Map<String, String>

        if (activityInstanceId != null) {
            try {
                kokiWorkflowEngine.complete(activityInstanceId, data)
            } catch(ex: HttpClientErrorException){

            }
        }
        return "redirect:/forms/$id/saved"
    }

    @ModelAttribute("page")
    fun getPage() = PageModel(
        name = PageName.FORM,
        title = "Form",
    )
}
