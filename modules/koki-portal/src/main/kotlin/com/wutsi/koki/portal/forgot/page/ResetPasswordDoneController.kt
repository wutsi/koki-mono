package com.wutsi.koki.portal.forgot.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/forgot/password/done")
class ForgotPasswordDoneController : AbstractPageController() {
    @GetMapping
    fun index(@RequestParam(name = "e") encodedEmail: String, model: Model): String {
        val email = String(Base64().decode(encodedEmail))
        model.addAttribute("email", email)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORGOT_PASSWORD_DONE,
                title = getMessage("page.forgot.password.meta.title"),
            )
        )
        return "forgot/password-done"
    }
}
