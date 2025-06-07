package com.wutsi.koki.portal.file.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@RequiresPermission(["file:admin"])
class SettingsFileController : AbstractPageController() {
    @GetMapping("/settings/files")
    fun show(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FILE_SETTINGS,
                title = "File Settings",
            )
        )
        return "files/settings/show"
    }
}
