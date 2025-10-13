package com.wutsi.koki.portal.home.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import io.lettuce.core.KillArgs.Builder.user
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController : AbstractPageController() {
    @GetMapping("/")
    fun show(model: Model): String {
        val user = userHolder.get()
        if (user != null) {
            model.addAttribute(
                "showToolbar",
                user.canManage("listing") ||
                    user.hasFullAccess("listing") ||
                    user.canManage("contact") ||
                    user.hasFullAccess("contact")
            )
        }

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.HOME,
                title = "Home",
            )
        )
        return "home/show"
    }
}
