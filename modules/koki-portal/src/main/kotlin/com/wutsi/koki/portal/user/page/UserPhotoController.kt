package com.wutsi.koki.portal.user.page

import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.user.model.UserForm
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/users/photo")
class UserPhotoController(
    private val fileService: FileService,
    private val userService: UserService,
) : AbstractPageController() {
    @GetMapping
    fun index(model: Model): String {
        val user = userHolder.get()

        model.addAttribute(
            "uploadUrl",
            fileService.uploadUrl(type = FileType.IMAGE),
        )
        model.addAttribute(
            "form",
            UserForm(
                photoUrl = user?.photoUrl,
            ),
        )
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.USER_PHOTO,
                title = user?.displayName ?: "",
            )
        )
        return "users/photo"
    }

    @GetMapping("/file")
    @ResponseBody
    fun file(@RequestParam id: Long): Map<String, String> {
        val file = fileService.get(id)
        return mapOf("url" to file.contentUrl)
    }

    @PostMapping
    fun submit(@ModelAttribute form: UserForm): String {
        val id = userHolder.get()?.id ?: -1
        userService.updatePhoto(id, form)
        return "redirect:/"
    }
}
