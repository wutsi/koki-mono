package com.wutsi.koki.portal.pub.listing.page

import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.place.model.PlaceModel
import com.wutsi.koki.portal.pub.place.service.PlaceService
import com.wutsi.koki.portal.pub.refdata.service.CategoryService
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/listings")
class ListingController(
    private val service: ListingService,
    private val categoryService: CategoryService,
    private val placeService: PlaceService,
) : AbstractPageController() {
    companion object {
        const val TOAST_TIMEOUT_MILLIS = 60 * 1000L
        const val TOAST_MESSAGE_SENT = "msg-sent"
    }

    @GetMapping("/{id}/{slug}")
    fun show(
        @PathVariable slug: String,
        @PathVariable id: Long,
        @RequestParam(name = "_toast", required = false) toast: String? = null,
        @RequestParam(name = "_ts", required = false) timestamp: Long? = null,
        model: Model,
    ): String {
        return show(id, toast, timestamp, model)
    }

    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        @RequestParam(name = "_toast", required = false) toast: String? = null,
        @RequestParam(name = "_ts", required = false) timestamp: Long? = null,
        model: Model,
    ): String {
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
        if (listing.amenities.size > 9) {
            val topAmenities = listing.amenities.filter { amenity -> amenity.top }.toMutableList()
            model.addAttribute("topAmenities", topAmenities)
        } else {
            model.addAttribute("topAmenities", listing.amenities)
        }

        /* Images */
        val heroImages = listing.images
            .filter { image -> image.contentUrl != listing.heroImageUrl }
            .take(4)
        model.addAttribute("heroImages21", heroImages.take(2))
        if (heroImages.size >= 4) {
            model.addAttribute("heroImages22", heroImages.subList(2, 4))
        }

        /* Place */
        if (listing.address?.neighbourhood?.id != null) {
            loadPlace(listing.address.neighbourhood.id, model)
        }

        /* Page */
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING,
                title = listOf(listing.title, listing.price?.displayText)
                    .joinToString(" - "),
                description = listing.summary,
                image = listing.heroImageUrl,
                url = listing.publicUrl,
                updatedTime = listing.publishedAt?.time ?: listing.createdAt.time
            )
        )

        /* Toast */
        loadToast(toast, timestamp, model)

        return "listings/show"
    }

    @GetMapping("/similar")
    fun similar(
        @RequestParam id: Long,
        model: Model,
    ): String {
        val listings = findSimilarListings(id, 10)
        if (listings.isNotEmpty()) {
            model.addAttribute("listings", listings)

            val neighborhood =
                listings.find { listing -> listing.address?.neighbourhood != null }?.address?.neighbourhood
            model.addAttribute("neighborhood", neighborhood)
        }
        return "listings/show-similar"
    }

    private fun findSimilarListings(id: Long, limit: Int): List<ListingModel> {
        // Get similar listing from neighborhood
        val neighborhood = service.getSimilar(
            id,
            sameNeighborhood = true,
            limit = limit,
        )
        if (neighborhood.size >= limit) {
            return neighborhood
        }

        // Supplement with city similar listings
        val excludeIds = neighborhood.map { listing -> listing.id }.toMutableSet()
        val city = service.getSimilar(
            id,
            sameCity = true,
            limit = 2 * limit,
        )
        return neighborhood + city.filter { listing -> !excludeIds.contains(listing.id) }
    }

    private fun loadToast(
        toast: String? = null,
        timestamp: Long? = null,
        model: Model,
    ) {
        if (toast == null || timestamp == null) {
            return
        }
        if (System.currentTimeMillis() - timestamp > TOAST_TIMEOUT_MILLIS) {
            return
        }

        val message = when (toast) {
            TOAST_MESSAGE_SENT -> getMessage("page.listing.toast.message-sent")
            else -> null
        }
        model.addAttribute("toastMessage", message)
    }

    private fun loadPlace(neighbourhoodId: Long, model: Model): PlaceModel? {
        val place = placeService.search(
            neighbourhoodIds = listOf(neighbourhoodId),
            types = listOf(PlaceType.NEIGHBORHOOD),
            statuses = listOf(PlaceStatus.PUBLISHED),
            limit = 1,
        ).items.firstOrNull() ?: return null

        model.addAttribute("place", place)
        return place
    }
}
