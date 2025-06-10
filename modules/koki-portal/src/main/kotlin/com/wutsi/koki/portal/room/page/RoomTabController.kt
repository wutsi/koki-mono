package com.wutsi.koki.portal.room.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Collections.emptyList

@Controller
@RequestMapping("/rooms/tab")
@RequiresPermission(["room"])
class RoomTabController(private val service: RoomService) : AbstractRoomController() {
    @GetMapping
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        model: Model
    ): String {
        model.addAttribute("testMode", testMode)
        model.addAttribute(
            "page",
            createPageModel(PageName.ROOM_TAB, "Rooms")
        )
        more(ownerId, ownerType, readOnly, model = model)
        return "rooms/tab/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val rooms = if (ownerType == ObjectType.ACCOUNT) {
            service.rooms(
                accountIds = listOf(ownerId),
                limit = limit,
                offset = offset
            )
        } else {
            emptyList()
        }

        model.addAttribute("showAccount", false)
        model.addAttribute("readOnly", readOnly == true)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        if (rooms.isNotEmpty()) {
            model.addAttribute("rooms", rooms)

            if (rooms.size >= limit) {
                val nextOffset = offset + limit
                var url = "/rooms/tab/more?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
                if (readOnly != null) {
                    url = "$url&read-only=$readOnly"
                }
                model.addAttribute("moreUrl", url)
            }
        }
        return "rooms/tab/more"
    }
}
