package com.wutsi.koki.room.web.room.page

import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.room.web.common.page.AbstractPageController
import com.wutsi.koki.room.web.common.page.PageName
import com.wutsi.koki.room.web.refdata.mapper.AmenityMapper
import com.wutsi.koki.room.web.refdata.service.CategoryService
import com.wutsi.koki.room.web.room.service.RoomService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/rooms")
class RoomController(
    private val service: RoomService,
    private val categoryService: CategoryService,
) : AbstractPageController() {
    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val room = service.room(id)
        model.addAttribute("room", room)

        val categoryIds = room.amenities.map { amenity -> amenity.categoryId }.distinct()
        val categories = categoryService.categories(
            type = CategoryType.AMENITY,
            active = true,
            limit = Integer.MAX_VALUE
        )
            .sortedBy { category -> category.name }
            .filter { category -> categoryIds.contains(category.id) }
        model.addAttribute("categories", categories)

        val topAmenities = room.amenities.filter { amenity ->
            AmenityMapper.TOP_AMENITIES_ICONS.keys.contains(amenity.id)
        }
        model.addAttribute("topAmenities", topAmenities)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM,
                title = room.title,
                description = room.summary,
            )
        )
        return "rooms/show"
    }
}
