package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.form.ListingForm
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.service.AmenityService
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.dto.LocationType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings/edit/address")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingAddressController(
    private val locationService: LocationService,
    private val ipService: GeoIpService,
    private val request: HttpServletRequest,
) : AbstractListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        val city = resolveCity()
        val parent = resolveParent(city)
        model.addAttribute("city", city)
        model.addAttribute("cityName",
            city?.let {
                parent?.let { "${city.name}, ${parent.name}" } ?: city.name
            }
        )

        loadCountries(model)
        model.addAttribute(
            "form",
            ListingForm(
                id = id,
                country = city?.country,
                cityId = city?.id,
            )
        )
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_ADDRESS,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        return "listings/address"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        return "redirect:/listings/edit/geo-location?id=${form.id}"
    }

    private fun resolveCity(): LocationModel? {
        try {
            val ip = getIp(request)
            val geo = ipService.resolve(ip)
            return if (geo != null) {
                locationService.locations(
                    country = geo.countryCode,
                    keyword = geo.city,
                    type = LocationType.CITY,
                    limit = 1,
                ).firstOrNull()
            } else {
                null
            }
        } catch (ex: Exception) {
            return null
        }
    }

    private fun resolveParent(city: LocationModel?): LocationModel? {
        try {
            return city?.parentId?.let { id -> locationService.location(id) }
        } catch (ex: Exception) {
            return null
        }
    }
}
