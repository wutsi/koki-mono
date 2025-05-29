package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.room.form.RoomForm
import com.wutsi.koki.portal.room.model.RoomModel
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException
import java.util.Currency

@Controller
@RequestMapping("/rooms")
@RequiresPermission(["room:manage"])
class EditRoomController(
    private val service: RoomService,
    private val locationService: LocationService,
) : AbstractRoomController() {
    @GetMapping("/{id}/edit")
    fun edit(
        @PathVariable id: Long,
        model: Model
    ): String {
        val room = service.room(id)
        val form = RoomForm(
            type = room.type,
            numberOfRooms = room.numberOfRooms,
            numberOfBathrooms = room.numberOfBathrooms,
            numberOfBeds = room.numberOfBeds,
            maxGuests = room.maxGuests,
            pricePerNight = room.pricePerNight?.value,
            pricePerMonth = room.pricePerMonth?.value,
            currency = room.pricePerNight?.currency,
            cityId = room.address?.city?.id,
            country = room.address?.country,
            street = room.address?.street,
            postalCode = room.address?.postalCode,
            neighborhoodId = room.neighborhood?.id,
            area = room.area,
            categoryId = room.category?.id,
            leaseTerm = room.leaseTerm,
            leaseType = room.leaseType,
            furnishedType = room.furnishedType,
            title = room.title,
            description = room.description,
            summary = room.summary,
        )
        return edit(room, form, model)
    }

    @PostMapping("/{id}/update")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute form: RoomForm,
        model: Model
    ): String {
        try {
            service.update(id, form)
            return "redirect:/rooms/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            val room = service.room(id)
            return edit(room, form, model)
        }
    }

    private fun edit(room: RoomModel, form: RoomForm, model: Model): String {
        model.addAttribute("room", room)
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM_EDIT,
                title = room.title ?: "Edit Room",
            )
        )

        loadCheckinCheckoutTime(model)
        loadCountries(model)
        model.addAttribute("types", RoomType.entries)
        model.addAttribute("leaseTerms", LeaseTerm.entries)
        model.addAttribute("leaseTypes", LeaseType.entries)
        model.addAttribute("furnishedTypes", FurnishedType.entries)

        val currency = Currency.getInstance(tenantHolder.get()!!.currency)
        model.addAttribute("currencies", listOf(currency))
        model.addAttribute("rooms", (1..20).toList())

        if (form.cityId != null) {
            val city = locationService.location(form.cityId)
            model.addAttribute("city", city)
        }
        if (form.neighborhoodId != null) {
            val neighborhood = locationService.location(form.neighborhoodId)
            model.addAttribute("neighborhood", neighborhood)
        }

        return "rooms/edit"
    }
}
