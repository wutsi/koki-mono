package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.room.form.GeoLocationForm
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/rooms")
@RequiresPermission(["room:manage", "room:full_access"])
class RoomMapController(
    private val service: RoomService,
) : AbstractRoomDetailsController() {
    @GetMapping("/{id}/map")
    fun show(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val room = service.room(id)
        model.addAttribute("room", room)
        model.addAttribute("form", GeoLocationForm(latitude = room.latitude, longitude = room.longitude))
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM_MAP,
                title = room.title ?: "Room - Title",
            )
        )

        model.addAttribute(
            "latitude",
            room.latitude
                ?: room.neighborhood?.latitude
                ?: room.address?.city?.latitude
        )
        model.addAttribute(
            "longitude",
            room.longitude
                ?: room.neighborhood?.longitude
                ?: room.address?.city?.longitude
        )
        model.addAttribute(
            "zoom",
            if (room.hasGeoLocation) {
                17
            } else if (room.neighborhood?.latitude != null && room.neighborhood.longitude != null) {
                16
            } else {
                13
            }
        )

        return "rooms/map"
    }

    @PostMapping("/{id}/map")
    fun save(
        @PathVariable id: Long,
        @ModelAttribute form: GeoLocationForm,
        model: Model
    ): String {
        try {
            service.save(id, form)
            return "redirect:/rooms/$id?_op=geo&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id, model)
        }
    }
}
