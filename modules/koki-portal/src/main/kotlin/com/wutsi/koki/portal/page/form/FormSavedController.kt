package com.wutsi.koki.portal.page.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.sdk.KokiForms
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FormSavedController(
    private val kokiForms: KokiForms,
) {
    @GetMapping("/forms/{id}/saved")
    fun saved(
        @PathVariable id: String,
        @RequestParam(required = false, name = "already-processed") alreadyProcessed: String? = null,
        model: Model
    ): String {
        val forms = kokiForms.search(ids = listOf(id)).forms
        val title = forms.firstOrNull()?.title
        model.addAttribute("title", title)
        model.addAttribute("alreadyProcessed", alreadyProcessed)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FORM_SAVED,
                title = title ?: "",
            )
        )
        return "forms/saved"
    }
}
