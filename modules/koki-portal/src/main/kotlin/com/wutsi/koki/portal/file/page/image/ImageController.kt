package com.wutsi.koki.portal.file.page.image

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/images")
@RequiresPermission(["image"])
class ImageController(
    private val service: FileService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val file = service.file(id)
        model.addAttribute("image", file)

        return "files/images/show"
    }
}
