package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/rooms")
@RequiresPermission(["room"])
class ListRoomController(private val service: RoomService) : AbstractRoomController() {
    @GetMapping
    fun list(
        @RequestParam(required = false) type: RoomType? = null,
        @RequestParam(required = false) status: RoomStatus? = null,
        model: Model,
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
    ): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM_LIST,
                title = "Rooms",
            )
        )

        model.addAttribute("type", type)
        model.addAttribute("types", RoomType.entries.filter { entry -> entry != RoomType.UNKNOWN })

        model.addAttribute("status", status)
        model.addAttribute("statuses", RoomStatus.entries.filter { entry -> entry != RoomStatus.UNKNOWN })

        more(type, status, model = model)
        loadToast(referer, toast, timestamp, operation, model)
        return "rooms/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false) type: RoomType? = null,
        @RequestParam(required = false) status: RoomStatus? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val rooms = service.rooms(
            types = type?.let { listOf(type) } ?: emptyList(),
            status = status,
            limit = limit,
            offset = offset
        )

        if (rooms.isNotEmpty()) {
            model.addAttribute("rooms", rooms)
            if (rooms.size >= limit) {
                val nextOffset = offset + limit
                var url = "/rooms/more?limit=$limit&offset=$nextOffset"
                type?.let { url = "$url&type=$type" }
                status?.let { url = "$url&status=$status" }
                model.addAttribute("moreUrl", url)
            }
        }

        return "rooms/more"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/rooms/$toast"))) {
            if (operation == "del") {
                model.addAttribute("toast", "Deleted!")
            }
        }
    }
}
