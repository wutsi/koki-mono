package com.wutsi.koki.portal.room.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/rooms")
@RequiresPermission(["room"])
class HeroImagePickerController(
    private val service: RoomService,
    private val fileService: FileService,
) : AbstractRoomDetailsController() {
    @GetMapping("/{id}/hero-image-picker")
    fun show(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val room = service.room(id)
        model.addAttribute("room", room)

        val images = fileService.files(
            type = FileType.IMAGE,
            status = FileStatus.APPROVED,
            ownerId = room.id,
            ownerType = ObjectType.ROOM,
            limit = 200,
        ).filter { image -> image.id != room.heroImage?.id }
        model.addAttribute("images", images)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM_HERO_IMAGE,
                title = room.title ?: "Room",
            )
        )

        return "rooms/hero-image-picker"
    }

    @RequiresPermission(["room:manage"])
    @GetMapping("/{id}/hero-image-picker/select")
    fun select(
        @PathVariable id: Long,
        @RequestParam(name = "file-id") fileId: Long,
    ): String {
        service.setHeroImage(id, fileId)
        return "redirect:/rooms/$id"
    }
}
