package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/signup/done")
class DoneController : AbstractPageController() {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SIGNUP_DONE,
                title = getMessage("page.signup.meta.title"),
            )
        )
        return "signup/done"
    }
}
