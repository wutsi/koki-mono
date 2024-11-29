package com.wutsi.koki.portal.page.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.FormService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class FormSavedController(
    private val service: FormService,
) : AbstractPageController() {
    @GetMapping("/forms/{id}/saved")
    fun saved(
        @PathVariable id: String,
        model: Model
    ): String {
        val form = service.form(id)

        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FORM_SAVED,
                title = form.title,
            )
        )
        return "forms/saved"
    }
}
