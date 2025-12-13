package com.wutsi.koki.portal.file.page.image

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

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
        val file = service.get(id)
        model.addAttribute("image", file)
        model.addAttribute("closeUrl", getCloseUrl(file))

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.IMAGE,
                title = file.title ?: "Image"
            )
        )
        return "files/images/show"
    }

    @GetMapping("/delete")
    @RequiresPermission(["image", "image:full_access"])
    fun delete(@RequestParam id: Long): String {
        val file = service.get(id)
        service.delete(id)

        val closeUrl = getCloseUrl(file)
        return closeUrl?.let { "redirect:$closeUrl" } ?: "redirect:/"
    }

    private fun getCloseUrl(file: FileModel): String? {
        val owner = file.owner
        if (owner != null) {
            val module = tenantHolder.get().modules
                .find { module -> module.objectType == owner.type }

            if (module?.homeUrl != null) {
                return "${module.homeUrl}/${owner.id}?tab=image"
            }
        }
        return null
    }
}
