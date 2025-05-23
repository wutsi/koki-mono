package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.service.AmenityService
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.net.URLEncoder

@Controller
@RequestMapping("/rooms")
@RequiresPermission(["room"])
class RoomController(
    private val service: RoomService,
    private val categoryService: CategoryService,
    private val amenityService: AmenityService
) : AbstractRoomDetailsController() {
    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        model: Model,
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
    ): String {
        val room = service.room(id)
        model.addAttribute("room", room)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM,
                title = room.title ?: "Room",
            )
        )

        val categories = categoryService.categories(
            type = CategoryType.AMENITY,
            active = true,
            limit = Integer.MAX_VALUE
        ).sortedBy { category -> category.name }
        model.addAttribute("amenityCategories", categories)

        val amenities = amenityService.amenities(limit = Integer.MAX_VALUE)
            .sortedBy { amenity -> amenity.name }
            .groupBy { amenity -> amenity.categoryId }
        model.addAttribute("amenitiesByCategoryId", amenities)

        loadToast(id, referer, toast, timestamp, operation, model)
        return "rooms/show"
    }

    @GetMapping("/{id}/delete")
    @RequiresPermission(["room:manage"])
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

    @GetMapping("/{id}/publish")
    @RequiresPermission(["room:manage"])
    fun publish(@PathVariable id: Long, model: Model): String {
        try {
            service.publish(id)
            return "redirect:/rooms/$id?_op=published&_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(id, model)
        }
    }

    private fun loadToast(
        id: Long,
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (toast == id && canShowToasts(timestamp, referer, listOf("/rooms/$id/edit", "/rooms/$id/map"))) {
            if (operation == "geo") {
                model.addAttribute("toast", "The geolocation has been saved")
            } else {
                model.addAttribute("toast", "Saved")
            }
        }
    }
}
