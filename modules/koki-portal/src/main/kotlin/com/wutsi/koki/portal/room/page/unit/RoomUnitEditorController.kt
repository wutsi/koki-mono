package com.wutsi.koki.portal.room.page.unit

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.room.form.RoomUnitForm
import com.wutsi.koki.portal.room.mapper.RoomUnitMapper
import com.wutsi.koki.portal.room.service.RoomUnitService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.room.dto.RoomUnitStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/room-units")
@RequiresPermission(["room-unit:manage"])
class RoomUnitEditorController(
    private val service: RoomUnitService,
    private val mapper: RoomUnitMapper,
) : AbstractPageController() {
    @GetMapping("/create")
    fun create(
        @RequestParam(name = "room-id") roomId: Long,
        model: Model
    ): String {
        model.addAttribute("form", RoomUnitForm(roomId = roomId))
        model.addAttribute("statuses", RoomUnitStatus.entries)
        model.addAttribute("submitUrl", "/room-units/add-new")
        loadFloors(model)

        return "rooms/units/editor"
    }

    @PostMapping("/add-new")
    @ResponseBody
    fun addNew(@ModelAttribute form: RoomUnitForm): Map<String, Any> {
        try {
            val id = service.create(form)
            return mapOf(
                "success" to true,
                "roomUnitId" to id,
            )
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            return mapOf(
                "success" to false,
                "error" to errorResponse.error.code
            )
        }
    }

    @GetMapping("/{id}/edit")
    fun edit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val roomUnit = service.roomUnit(id)
        model.addAttribute(
            "form",
            RoomUnitForm(
                roomId = roomUnit.roomId,
                floor = roomUnit.floor,
                status = roomUnit.status,
                number = roomUnit.number,
            )
        )
        model.addAttribute("statuses", RoomUnitStatus.entries)
        model.addAttribute("submitUrl", "/room-units/$id/update")
        loadFloors(model)

        return "rooms/units/editor"
    }

    @PostMapping("/{id}/update")
    @ResponseBody
    fun update(@PathVariable id: Long, @ModelAttribute form: RoomUnitForm): Map<String, Any> {
        try {
            service.update(id, form)
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

    private fun loadFloors(model: Model) {
        model.addAttribute(
            "floors",
            (0..100).toList().map { i -> mapper.toFloorText(i) }
        )
    }
}
