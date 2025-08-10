package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.form.ListingForm
import com.wutsi.koki.portal.refdata.service.AmenityService
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings/edit/amenities")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingAmenitiesController(
    private val categoryService: CategoryService,
    private val amenityService: AmenityService
) : AbstractListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        model.addAttribute(
            "form",
            ListingForm(
                id = id,
            )
        )
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_AMENITIES,
                title = getMessage("page.listing.edit.meta.title"),
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

        return "listings/amenities"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        return "redirect:/listings/edit/address?id=${form.id}"
    }
}
