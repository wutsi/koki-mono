package com.wutsi.koki.portal.room.page.amenity

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.refdata.service.AmenityService
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/room-amenities/tab")
@RequiresPermission(["room-amenity"])
class RoomAmenityTabController(
    private val service: RoomService,
    private val categoryService: CategoryService,
    private val amenityService: AmenityService
) : AbstractPageController() {
    @GetMapping
    fun show(
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        @RequestParam(required = false, name = "test-mode") testMode: String? = null,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean? = null,
        model: Model
    ): String {
        model.addAttribute("testMode", testMode)
        model.addAttribute("readOnly", readOnly)

        if (ownerType == ObjectType.ROOM) {
            val room = service.room(ownerId)
            model.addAttribute("room", room)

            val categories = categoryService.search(
                type = CategoryType.AMENITY,
                active = true,
                limit = Integer.MAX_VALUE
            ).sortedBy { category -> category.name }
            model.addAttribute("amenityCategories", categories)

            val amenities = amenityService.amenities(limit = Integer.MAX_VALUE)
                .sortedBy { amenity -> amenity.name }
                .groupBy { amenity -> amenity.categoryId }
            model.addAttribute("amenitiesByCategoryId", amenities)
        }
        return "rooms/amenities/tab/show"
    }

    @ResponseBody
    @GetMapping("/toggle")
    @RequiresPermission(["room-amenity:manage"])
    fun toggle(
        @RequestParam(name = "room-id") roomId: Long,
        @RequestParam(name = "amenity-id") amenityId: Long,
        @RequestParam checked: Boolean,
    ): Map<String, Any> {
        try {
            if (checked) {
                service.addAmenity(roomId, amenityId)
            } else {
                service.removeAmenity(roomId, amenityId)
            }
            return mapOf("success" to true)
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            return mapOf(
                "success" to false,
                "error" to errorResponse.error.code
            )
        }
    }
}
