package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.signup.form.PhotoForm
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

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
