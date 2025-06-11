package com.wutsi.koki.room.web.room.page

import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.web.common.page.AbstractPageController
import com.wutsi.koki.room.web.common.page.PageName
import com.wutsi.koki.room.web.geoip.service.CurrentGeoIPHolder
import com.wutsi.koki.room.web.message.form.SendMessageForm
import com.wutsi.koki.room.web.message.service.MessageService
import com.wutsi.koki.room.web.refdata.mapper.AmenityMapper
import com.wutsi.koki.room.web.refdata.service.CategoryService
import com.wutsi.koki.room.web.room.service.RoomService
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/rooms")
class RoomController(
    private val service: RoomService,
    private val categoryService: CategoryService,
    private val messageService: MessageService,
    private val currentGeoIp: CurrentGeoIPHolder,
) : AbstractPageController() {
    @GetMapping("/{id}/{title}")
    fun show(@PathVariable id: Long, @PathVariable title: String, model: Model): String {
        return show(id, model)
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        /* Room */
        val room = service.room(id)
        if (room.status != RoomStatus.PUBLISHED) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), "Room not published: ${room.status}")
        }
        model.addAttribute("room", room)

        /* Amenities */
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
        }.toMutableList()
        if (topAmenities.size < 10) {
            val amenitiesByCategories = room.amenities.groupBy { amenity -> amenity.categoryId }
            amenitiesByCategories.keys.forEach { categoryId ->
                topAmenities.add(amenitiesByCategories[categoryId]!!.first())
            }
        }
        model.addAttribute("topAmenities", topAmenities.distinctBy { amenity -> amenity.id })

        /* Images */
        val heroImages = room.images
            .filter { image -> image.id != room.heroImage?.id }
            .take(4)
        model.addAttribute("heroImages21", heroImages.take(2))
        if (heroImages.size >= 4) {
            model.addAttribute("heroImages22", heroImages.subList(2, 4))
        }

        /* Email */
        model.addAttribute(
            "form",
            SendMessageForm(
                roomId = room.id
            )
        )

        /* Geo IP */
        model.addAttribute("geoip", currentGeoIp.get())

        /* Page */
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM,
                title = room.title ?: "",
                description = room.summary,
                image = room.heroImage?.contentUrl,
                url = "$baseUrl${room.url}",
            )
        )
        return "rooms/show"
    }

    @PostMapping("/send")
    @ResponseBody
    fun send(@ModelAttribute form: SendMessageForm): Map<String, Any> {
        try {
            messageService.send(form)
            return mapOf("success" to true)
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            return mapOf(
                "success" to false,
                "error" to response.error.code
            )
        }
    }
}
