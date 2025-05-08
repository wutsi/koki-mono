package com.wutsi.koki.portal.file.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
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
    private val roomService: RoomService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        @RequestParam("owner-id") ownerId: Long,
        @RequestParam("owner-type") ownerType: ObjectType,
        @RequestParam("read-only", required = false) readOnly: Boolean = false,
        model: Model,
    ): String {
        val file = service.file(id)
        model.addAttribute("image", file)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("readOnly", readOnly)

        val files = service.files(
            ownerId = ownerId, ownerType = ownerType, fileType = ObjectType.IMAGE, limit = Integer.MAX_VALUE
        )
        model.addAttribute("images", files)

        model.addAttribute(
            "page", PageModel(
                name = PageName.IMAGE, title = file.name
            )
        )

        loadOwnerInformation(ownerId, ownerType, model)
        return "images/show"
    }

    @GetMapping("/{id}/delete")
    @RequiresPermission(["image:manage"])
    fun delete(
        @PathVariable id: Long,
        @RequestParam("owner-id") ownerId: Long,
        @RequestParam("owner-type") ownerType: ObjectType,
        model: Model
    ): String {
        try {
            val module = tenantHolder.get()?.modules?.find { module -> module.objectType == ownerType }!!

            service.delete(id)
            return "redirect:" + getOwnerUrl(ownerId, module)
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id, ownerId, ownerType, false, model)
        }
    }

    private fun loadOwnerInformation(ownerId: Long, ownerType: ObjectType, model: Model) {
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("ownerId", ownerId)

        val module = tenantHolder.get()?.modules?.find { module -> module.objectType == ownerType }
        module?.let {
            model.addAttribute("ownerModule", module)
            model.addAttribute("ownerUrl", getOwnerUrl(ownerId, module))
        }

        val owner = when (ownerType) {
            ObjectType.ROOM -> roomService.room(ownerId, fullGraph = false)
            else -> null
        }
        model.addAttribute("owner", owner)
    }

    private fun getOwnerUrl(ownerId: Long, module: ModuleModel): String {
        return "${module.homeUrl}/$ownerId?tab=image"
    }
}
