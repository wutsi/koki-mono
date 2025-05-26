package com.wutsi.koki.portal.room.page.unit

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.room.service.RoomUnitService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/room-units/tab")
@RequiresPermission(["room-unit"])
class RoomUnitTabController(
    private val service: RoomUnitService
) : AbstractPageController() {
    @GetMapping
    fun list(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        model: Model
    ): String {
        model.addAttribute("testMode", testMode)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        more(ownerId, ownerType, testMode, readOnly, 20, 0, model)
        return "rooms/units/tab/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model
    ): String {
        val roomUnits = if (ownerType == ObjectType.ROOM) {
            service.roomUnits(
                roomId = ownerId,
                limit = limit,
                offset = offset
            )
        } else {
            emptyList()
        }

        model.addAttribute("readOnly", readOnly ?: false)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("testMode", testMode)
        if (roomUnits.isNotEmpty()) {
            model.addAttribute("roomUnits", roomUnits)

            if (roomUnits.size >= limit) {
                val nextOffset = offset + limit
                var url =
                    "/room-units/tab/more.html?limit=$limit&offset=$nextOffset&owner-id=$ownerId&owner-type=$ownerType"
                if (readOnly != null) {
                    url = "$url&read-only=$readOnly"
                }
                model.addAttribute("moreUrl", url)
            }
        }
        return "rooms/units/tab/more"
    }

    @GetMapping("/delete")
    @ResponseBody
    @RequiresPermission(["room-unit:manage"])
    fun delete(@RequestParam id: Long): Map<String, Any> {
        try {
            service.delete(id)
            return mapOf(
                "success" to true,
            )
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            return mapOf(
                "success" to false,
                "error" to errorResponse.error.code
            )
        }
    }
}
