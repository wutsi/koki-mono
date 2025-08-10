package com.wutsi.koki.portal.tenant.page.settings.type

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.service.JuridictionService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.form.BusinessForm
import com.wutsi.koki.portal.tenant.service.BusinessService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["tenant:admin"])
class SettingsEditBusinessController(
    private val service: BusinessService,
    private val juridictionService: JuridictionService,
) : AbstractPageController() {
    @GetMapping("/settings/tenant/business/edit")
    fun edit(model: Model): String {
        val form = try {
            val business = service.business()
            BusinessForm(
                companyName = business.companyName,
                phone = business.phone,
                fax = business.fax,
                email = business.email,
                website = business.website,
                addressStreet = business.address?.street,
                addressCountry = business.address?.country,
                addressCityId = business.address?.city?.id,
                addressPostalCode = business.address?.postalCode,
                juridictionIds = business.juridictions.map { juridiction -> juridiction.id },
            )
        } catch (ex: Exception) {
            BusinessForm()
        }

        return edit(form, model)
    }

    fun edit(form: BusinessForm, model: Model): String {
        model.addAttribute("form", form)

        val juridictions = juridictionService.juridictions(limit = Integer.MAX_VALUE)
        model.addAttribute("juridictions", juridictions)

        loadCountries(model)

        val city = form.addressCityId?.let { id -> locationService.location(id) }
        model.addAttribute("city", city)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.TENANT_SETTINGS_BUSINESS_EDIT,
                title = "Business",
            )
        )

        return "tenant/settings/business/edit"
    }

    @PostMapping("/settings/tenant/business/save")
    fun save(@ModelAttribute form: BusinessForm, model: Model): String {
        try {
            service.save(form)

            val now = System.currentTimeMillis()
            return "redirect:/settings/tenant/business?_toast=$now&_ts=$now"
        } catch (ex: HttpClientErrorException) {
            val response = toErrorResponse(ex)
            model.addAttribute("error", response.error.code)
            return edit(form, model)
        }
    }
}
