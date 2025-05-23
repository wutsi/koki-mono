package com.wutsi.koki.room.web.error.page

import com.wutsi.koki.room.web.common.model.PageModel
import com.wutsi.koki.room.web.common.page.AbstractPageController
import com.wutsi.koki.room.web.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class SuspendedController : AbstractPageController() {
    @GetMapping("/error/suspended")
    fun error(model: Model): String {
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.ERROR_SUSPENDED,
                title = "Suspended"
            )
        )
        return "/error/suspended"
    }
}
