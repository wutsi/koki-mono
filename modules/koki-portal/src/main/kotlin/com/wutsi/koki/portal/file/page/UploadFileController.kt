package com.wutsi.koki.portal.file.page

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping
class UploadFileController : AbstractPageController() {
    @GetMapping("/files/upload")
    fun download(
        @RequestParam(name = "upload-url") uploadUrl: String,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        model: Model,
    ): String {
        model.addAttribute("uploadUrl", uploadUrl)
        model.addAttribute("returnUrl", returnUrl)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.UPLOAD,
                title = "Upload Files"
            )
        )
        return "files/upload"
    }
}
