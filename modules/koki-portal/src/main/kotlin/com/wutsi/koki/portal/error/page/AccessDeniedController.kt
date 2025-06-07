package com.wutsi.koki.portal.error.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class AccessDeniedController : AbstractPageController() {
    @GetMapping("/error/access-denied")
    fun error(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ERROR_403,
                title = "Access Denied"
            )
        )
        return "error/access-denied"
    }
}
