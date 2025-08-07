package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.platform.geoip.GeoIpService
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.signup.form.ProfileForm
import com.wutsi.koki.refdata.dto.LocationType
import io.lettuce.core.KillArgs.Builder.id
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Locale

@Controller
@RequestMapping("/signup/profile")
class ProfileController(
    private val ipService: GeoIpService,
    private val locationService: LocationService,
    private val request: HttpServletRequest,
) : AbstractPageController() {
    @GetMapping
    fun index(model: Model): String {
        val city = resolveCity()
        val parent = resolveParent(city)
        model.addAttribute("city", city)
        model.addAttribute("cityName",
            city?.let {
                parent?.let { "${city.name}, ${parent.name}" } ?: city.name
            }
        )

        model.addAttribute(
            "form",
            ProfileForm(
                name = "Ray Sponsible",
                email = "ray.sponsible@gmail.com",
                country = city?.country
                    ?: tenantHolder.get()?.locale?.let { locale -> Locale.forLanguageTag(locale) }?.country,
                cityId = city?.id
            )
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SIGNUP_PROFILE,
                title = getMessage("page.signup.meta.title"),
            )
        )
        loadCountries(model)
        return "signup/profile"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ProfileForm, model: Model): String {
        return "redirect:/signup/photo"
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
