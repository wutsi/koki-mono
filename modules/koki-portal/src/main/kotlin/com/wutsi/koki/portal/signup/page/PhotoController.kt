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
@RequestMapping("/signup/photo")
class PhotoController(
    private val fileService: FileService
) : AbstractPageController() {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("uploadUrl", fileService.uploadUrl(type = FileType.IMAGE))
        model.addAttribute("form", PhotoForm())
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SIGNUP_PHOTO,
                title = getMessage("page.signup.meta.title"),
            )
        )
        return "signup/photo"
    }

    @GetMapping("/file")
    @ResponseBody
    fun file(@RequestParam id: Long): Map<String, String> {
        val file = fileService.file(id)
        return mapOf("url" to file.contentUrl)
    }

    @PostMapping
    fun submit(@ModelAttribute form: PhotoForm, model: Model): String {
        return "redirect:/signup/done"
    }
}
