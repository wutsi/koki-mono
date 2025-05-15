package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.room.form.RoomForm
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.room.dto.RoomType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException
import java.util.Currency

@Controller
@RequestMapping("/rooms")
@RequiresPermission(["room:manage"])
class CreateRoomController(
    private val service: RoomService,
    private val locationService: LocationService,
) : AbstractRoomController() {
    @GetMapping("/create")
    fun create(model: Model): String {
        return create(
            RoomForm(
                currency = tenantHolder.get()?.currency ?: "",
                checkinTime = "15:00",
                checkoutTime = "12:00",
            ),
            model,
        )
    }

    @PostMapping("/add-new")
    fun addNew(
        @ModelAttribute form: RoomForm,
        model: Model
    ): String {
        try {
            val id = service.create(form)
            return "redirect:/rooms/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, model)
        }
    }

    private fun create(form: RoomForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM_CREATE,
                title = "Create Room",
            )
        )

        loadCheckinCheckoutTime(model)
        loadCountries(model)
        model.addAttribute("types", RoomType.entries.filter { entry -> entry != RoomType.UNKNOWN })

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

        return "rooms/create"
    }
}
