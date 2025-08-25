package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.FurnitureType
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
) : AbstractEditListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("form", toListingForm(listing))

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_AMENITIES,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        model.addAttribute("furnitureTypes", FurnitureType.entries)

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

        return "listings/edit-amenities"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm): String {
        listingService.updateAmenities(form)
        return "redirect:/listings/edit/address?id=${form.id}"
    }
}
