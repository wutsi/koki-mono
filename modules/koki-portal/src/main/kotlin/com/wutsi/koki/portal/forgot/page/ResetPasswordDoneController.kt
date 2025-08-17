package com.wutsi.koki.portal.forgot.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/forgot/password/reset/done")
class ResetPasswordDoneController : AbstractPageController() {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORGOT_PASSWORD_RESET_DONE,
                title = getMessage("page.forgot.password.meta.title"),
            )
        )
        return "forgot/password-reset-done"
    }
}
