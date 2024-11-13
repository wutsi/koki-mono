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
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class FormController(
    private val kokiForms: KokiForms,
    private val kokiFormData: KokiFormData,
) : AbstractPageController() {
    @GetMapping("/forms/{id}")
    fun show(
        @PathVariable id: String,
        model: Model
    ): String {
        val formHtml = kokiForms.html(formId = id)
        model.addAttribute("formHtml", formHtml)

        val forms = kokiForms.search(ids = listOf(id)).forms
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FORM,
                title = forms.firstOrNull()?.title ?: "Form",
            )
        )
        return "forms/index"
    }

    @PostMapping("/forms/{id}")
    fun submit(
        @PathVariable id: String,
        request: HttpServletRequest
    ): String {
        val data = request.parameterMap
            .map { entry ->
                if (entry.value.size == 1) {
                    entry.key to entry.value[0]
                } else {
                    entry.key to entry.value
                }
            }
            .toMap()
            as Map<String, Any>

        kokiFormData.submit(id, data)
        return "redirect:/forms/$id/saved"
    }

    @ModelAttribute("page")
    fun getPage() = PageModel(
        name = PageName.FORM,
        title = "Form",
    )
}
