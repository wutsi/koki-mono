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
@RequestMapping("/listings/edit/price")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingPriceController : AbstractListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        model.addAttribute(
            "form",
            ListingForm(
                id = id,
                listingType = ListingType.RENTAL,
                currency = tenantHolder.get()?.currency
            )
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_ADDRESS,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        return "listings/price"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        return "redirect:/listings/edit/contract?id=${form.id}"
    }
}
