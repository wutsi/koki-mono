package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.form.AIListingForm
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestClientException

@Controller
@RequestMapping("/listings/create/ai")
@RequiresPermission(["listing:manage", "listing:full_access"])
class CreateAIListingController : AbstractListingController() {
    @GetMapping
    fun create(model: Model): String {
        val city = userHolder.get()?.city ?: resolveCity()
        val form = AIListingForm(
            cityId = city?.id,
            country = city?.country ?: tenantHolder.get().country,
        )
        return create(form, model, city)
    }

    private fun create(form: AIListingForm, model: Model, city: LocationModel?): String {
        val xcity = city ?: form.cityId?.let { id -> locationService.get(id) }
        if (xcity != null) {
            val parent = resolveParent(xcity)
            model.addAttribute("city", xcity)
            model.addAttribute("cityName", parent?.let { "${xcity.name}, ${parent.name}" } ?: xcity.name)
        }
        loadCountries(model)

        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_CREATE_AI,
                title = getMessage("page.listing.create.meta.title"),
            )
        )
        return "listings/create-ai"
    }

    @PostMapping
    fun submit(@ModelAttribute form: AIListingForm, model: Model): String {
        try {
            val id = listingService.create(form)
            return "redirect:/listings/$id"
        } catch (ex: RestClientException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", toErrorMessage(errorResponse))
            return create(form, model, null)
        }
    }
}
