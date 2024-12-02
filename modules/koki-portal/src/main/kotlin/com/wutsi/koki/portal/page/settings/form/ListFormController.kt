package com.wutsi.koki.portal.page.settings.form

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.FormService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import kotlin.collections.isNotEmpty

@Controller
class ListFormController(private val service: FormService) {
    @GetMapping("/settings/forms")
    fun list(model: Model): String {
        val forms = service.forms()
        if (forms.isNotEmpty()) {
            model.addAttribute("forms", forms)
        }
        model.addAttribute(
            "page",
            PageModel(name = PageName.SETTINGS_FORM_LIST, title = "Forms"),
        )
        return "settings/forms/list"
    }
}
