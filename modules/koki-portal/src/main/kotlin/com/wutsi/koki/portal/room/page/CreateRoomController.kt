package com.wutsi.koki.portal.room.page

import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.room.form.RoomForm
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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.text.SimpleDateFormat
import java.util.Currency

@Controller
@RequestMapping("/rooms")
@RequiresPermission(["room:manage", "room:full_access"])
class CreateRoomController(
    private val service: RoomService,
    private val accountService: AccountService,
) : AbstractRoomController() {
    @GetMapping("/create")
    fun create(
        @RequestParam(required = false, name = "account-id") accountId: Long? = null,
        @RequestParam(required = false, name = "copy-id") copyId: Long? = null,
        model: Model
    ): String {
        if (copyId != null) {
            val form = createRoom(copyId)
            val account = accountService.account(form.accountId)
            return create(account, form, model)
        } else {
            val account = accountId?.let { id -> accountService.account(id) }
            val form = createRoom(account)
            return create(account, form, model)
        }
    }

    private fun createRoom(account: AccountModel?): RoomForm {
        var accountId = account?.id
        var country = account?.shippingAddress?.country
        var cityId = account?.shippingAddress?.city?.id
        if (accountId == null) {
            // Get all the accounts of the user
            val accounts = accountService.accounts(
                managedByIds = userHolder.get()?.let { user -> listOf(user.id) } ?: emptyList(),
                limit = 2,
            )

            // Get the primary account details
            if (accounts.size == 1) {
                accountId = accounts.first().id
                val act = accountService.account(accountId)
                country = act.shippingAddress?.country
                cityId = act.shippingAddress?.city?.id
            }
        }
        return RoomForm(
            currency = tenantHolder.get()?.currency,
            accountId = accountId ?: -1,
            country = country,
            cityId = cityId,
        )
    }

    private fun createRoom(copyId: Long): RoomForm {
        val room = service.room(copyId)
        return RoomForm(
            accountId = room.account.id,
            type = room.type,
            furnishedType = room.furnishedType,
            leaseType = room.leaseType,
            leaseTerm = room.leaseTerm,
            numberOfRooms = room.numberOfRooms,
            numberOfBeds = room.numberOfBeds,
            numberOfBathrooms = room.numberOfBathrooms,
            maxGuests = room.maxGuests,
            currency = tenantHolder.get()?.currency,
            country = room.address?.country,
            cityId = room.address?.city?.id,
            street = room.address?.street,
            postalCode = room.address?.postalCode,
            neighborhoodId = room.neighborhood?.id,
            pricePerMonth = room.pricePerMonth?.value,
            pricePerNight = room.pricePerNight?.value,
            area = room.area,
            categoryId = room.category?.id,
            checkinTime = room.checkinTime,
            checkoutTime = room.checkinTime,
            title = null,
            summary = null,
            description = null,
            longitude = room.longitude,
            latitude = room.latitude,
            visitFees = room.visitFees?.value,
            advanceRent = room.advanceRent,
            yearOfConstruction = room.yearOfConstruction,
            leaseTermDuration = room.leaseTermDuration,
            dateOfAvailability = room.dateOfAvailability?.let { date -> SimpleDateFormat("yyyy-MM-dd").format(date) },
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

            val account = accountService.account(form.accountId)
            return create(account, form, model)
        }
    }

    private fun create(account: AccountModel?, form: RoomForm, model: Model): String {
        model.addAttribute("account", account)

        model.addAttribute("form", form)
        if (form.cityId != null) {
            val city = locationService.get(form.cityId)
            model.addAttribute("city", city)
        }
        if (form.neighborhoodId != null) {
            val neighborhood = locationService.get(form.neighborhoodId)
            model.addAttribute("neighborhood", neighborhood)
        }
        if (form.accountId > 0) {
            model.addAttribute("account", accountService.account(id = form.accountId, fullGraph = false))
        }

        loadCheckinCheckoutTime(model)
        loadLeaseTermDurations(model)
        loadYearOfConstructions(model)
        loadCountries(model)
        model.addAttribute("types", RoomType.entries)
        model.addAttribute("leaseTerms", LeaseTerm.entries)
        model.addAttribute("leaseTypes", LeaseType.entries)
        model.addAttribute("furnishedTypes", FurnishedType.entries)

        val currency = Currency.getInstance(tenantHolder.get()!!.currency)
        model.addAttribute("currencies", listOf(currency))
        model.addAttribute("rooms", (0..20).toList())

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.ROOM_CREATE,
                title = "Create Room",
            )
        )
        return "rooms/create"
    }
}
