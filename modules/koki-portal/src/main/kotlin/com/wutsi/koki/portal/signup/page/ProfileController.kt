package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.signup.form.ProfileForm
import io.lettuce.core.KillArgs.Builder.id
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Locale

@Controller
@RequestMapping("/signup/profile")
class ProfileController : AbstractPageController() {
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
}
