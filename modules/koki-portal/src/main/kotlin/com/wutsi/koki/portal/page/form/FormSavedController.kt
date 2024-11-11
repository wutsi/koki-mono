package com.wutsi.koki.portal.page.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.rest.KokiForms
import com.wutsi.koki.portal.rest.KokiWorkflowEngine
import com.wutsi.koki.portal.rest.TenantService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

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
        val form = kokiForms.html(
            formId = id,
            activityInstanceId = activityInstanceId,
            submitUrl = "/forms/$id?aiid=" + (activityInstanceId ?: ""),
            tenantId = tenantService.id()
        )
        model.addAttribute("form", form)

        return "forms/index"
    }

    @PostMapping("/forms/{id}")
    fun submit(
        @PathVariable id: String,
        @RequestParam(required = false, name = "aiid") activityInstanceId: String? = null,
        @ModelAttribute data: Map<String, String>,
    ): String {
        if (activityInstanceId != null) {
            kokiWorkflowEngine.complete(activityInstanceId, data)
        }
        return "redirect:/forms/saved"
    }

    @ModelAttribute("page")
    fun getPage() = PageModel(
        name = PageName.FORM,
        title = "Form",
    )
}
