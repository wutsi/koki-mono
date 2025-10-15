package com.wutsi.koki.portal.pub.listing.page

import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.refdata.service.CategoryService
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/listings")
class ListingController(
    private val service: ListingService,
    private val categoryService: CategoryService,
) : AbstractPageController() {
    @GetMapping("/{id}/{slug}")
    fun show(@PathVariable slug: String, @PathVariable id: Long, model: Model): String {
        return show(id, model)
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val listing = service.get(id)
        if (!listing.statusActive && !listing.statusSold) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(404))
        }
        model.addAttribute("listing", listing)

        /* Amenities */
        val categoryIds = listing.amenities.map { amenity -> amenity.categoryId }.distinct()
        val categories = categoryService.search(
            type = CategoryType.AMENITY,
            active = true,
            limit = Integer.MAX_VALUE
        )
            .sortedBy { category -> category.name }
            .filter { category -> categoryIds.contains(category.id) }
        model.addAttribute("categories", categories)

        /* Top Amenities */
        val topAmenities = listing.amenities.filter { amenity -> amenity.top }.toMutableList()
        model.addAttribute("topAmenities", topAmenities)

        /* Images */
        val heroImages = listing.images
            .filter { image -> image.contentUrl != listing.heroImageUrl }
            .take(4)
        model.addAttribute("heroImages21", heroImages.take(2))
        if (heroImages.size >= 4) {
            model.addAttribute("heroImages22", heroImages.subList(2, 4))
        }

        /* Page */
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING,
                title = listOf(listing.title, listing.price?.displayText)
                    .filterNotNull()
                    .joinToString(" - "),
                description = listing.summary,
                image = listing.heroImageUrl,
                url = listing.publicUrl,
                updatedTime = listing.publishedAt?.time ?: listing.createdAt.time
            )
        )

        return "listings/show"
    }
}
