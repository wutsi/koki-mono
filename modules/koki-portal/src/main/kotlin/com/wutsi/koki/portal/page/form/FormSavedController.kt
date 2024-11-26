package com.wutsi.koki.portal.page.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.sdk.KokiForms
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class FormSavedController(
    private val kokiForms: KokiForms,
) : AbstractPageController() {
    @GetMapping("/forms/{id}/saved")
    fun saved(
        @PathVariable id: String,
        model: Model
    ): String {
        val form = kokiForms.getForm(id).form
        val title = form.title

        model.addAttribute("title", title)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FORM_SAVED,
                title = title,
            )
        )
        return "forms/saved"
    }
}
