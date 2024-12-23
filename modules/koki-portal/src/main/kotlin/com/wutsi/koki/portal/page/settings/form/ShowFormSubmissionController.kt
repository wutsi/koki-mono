package com.wutsi.koki.portal.page.settings.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.FormService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ShowFormSubmissionController(private val service: FormService) : AbstractPageController() {
    @GetMapping("/settings/forms/submissions/{id}")
    fun show(
        @PathVariable id: String,
        model: Model,
    ): String {
        val submission = service.submission(id)
        model.addAttribute("submission", submission)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_FORM_SUBMISSION,
                title = "Form Submission"
            ),
        )
        return "settings/forms/submission"
    }
}
