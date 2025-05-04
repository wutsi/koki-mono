package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException
import java.net.URLEncoder

@Controller
@RequestMapping("/rooms")
@RequiresPermission(["room"])
class RoomController(private val service: RoomService) : AbstractRoomDetailsController() {
    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        model: Model
    ): String {
        val room = service.room(id)
        model.addAttribute("room", room)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM,
                title = room.title,
            )
        )

        return "rooms/show"
    }

    @GetMapping("/{id}/delete")
    @RequiresPermission(["product:manage"])
    fun delete(@PathVariable id: Long, model: Model): String {
        try {
            val room = service.room(id, fullGraph = false)
            service.delete(id)
            return "redirect:/rooms?_op=del&_toast=$id&_ts=" + System.currentTimeMillis() +
                "&_title=" + URLEncoder.encode(room.title, "utf-8")
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id, model)
        }
    }
}
