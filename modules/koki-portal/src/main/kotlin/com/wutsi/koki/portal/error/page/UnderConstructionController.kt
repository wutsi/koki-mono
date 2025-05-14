package com.wutsi.koki.portal.error.page

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UnderConstructionController : AbstractPageController() {
    @GetMapping("/error/under-construction")
    fun suspended(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ERROR_UNDER_CONSTRUCTION,
                title = "Under Construction"
            )
        )
        return "/error/under-construction"
    }
}
