package com.wutsi.koki.portal.page.form.widget

import com.wutsi.koki.portal.service.FormService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FormSubmissionsWidgetController(
    private val service: FormService,
) {
    @GetMapping("/forms/widgets/submissions")
    fun show(
        @RequestParam(name = "form-id") formId: String,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        more(formId, limit, offset, model)
        return "forms/widgets/submissions"
    }

    @GetMapping("/forms/widgets/submissions/more")
    fun more(
        @RequestParam(name = "form-id") formId: String,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val submissions = service.submissions(
            formId = formId,
            offset = offset,
            limit = limit
        )
        if (submissions.isNotEmpty()) {
            model.addAttribute("submissions", submissions)

            if (submissions.size >= limit) {
                val nextOffset = offset + limit
                val moreUrl = "/forms/widgets/submissions/more?form-id=$formId&limit=$limit&offset=$nextOffset"
                model.addAttribute("moreUrl", moreUrl)
            }
        }

        return "forms/widgets/submissions-more"
    }
}
