package com.wutsi.koki.portal.file.page.image

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/images")
@RequiresPermission(["image"])
class ImageController(
    private val service: FileService,
    private val listingService: ListingService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val file = service.get(id)
        model.addAttribute("image", file)
        model.addAttribute("closeUrl", getCloseUrl(file))
        model.addAttribute("readOnly", isReadOnly(file))

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.IMAGE,
                title = file.title ?: "-"
            )
        )
        return "files/images/show"
    }

    @GetMapping("/delete")
    @RequiresPermission(["image", "image:full_access"])
    fun delete(@RequestParam id: Long): String {
        val file = service.get(id)
        if (isReadOnly(file)) {
            throw HttpClientErrorException(HttpStatus.FORBIDDEN)
        }

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

    private fun isReadOnly(file: FileModel): Boolean {
        val owner = file.owner ?: return false

        try {
            return when (owner.type) {
                ObjectType.LISTING -> listingService.get(owner.id, fullGraph = false).readOnly
                else -> false
            }
        } catch (ex: Exception) {
            return false
        }
    }
}
