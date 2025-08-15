package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.signup.form.SignupForm
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.util.Locale

@Controller
@RequestMapping("/signup/profile")
class ProfileController(
    private val categoryService: CategoryService,
) : AbstractSignupController() {
    @GetMapping
    fun index(@RequestParam id: Long, model: Model): String {
        val user = resolveUser(id)
        val city = loadCity(null, model)
        val form = SignupForm(
            id = user.id,
            name = user.displayName,
            email = user.email,
            country = city?.country
                ?: tenantHolder.get()?.locale?.let { locale -> Locale.forLanguageTag(locale) }?.country,
            cityId = city?.id
        )
        return index(form, model, city)
    }

    private fun index(form: SignupForm, model: Model, city: LocationModel?): String {
        model.addAttribute("form", form)

        if (form.cityId != city?.id) {
            loadCity(form.cityId, model)
        }

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SIGNUP_PROFILE,
                title = getMessage("page.signup.meta.title"),
            )
        )
        loadCountries(model)
        loadCategories(model)
        return "signup/profile"
    }

    @PostMapping
    fun submit(@ModelAttribute form: SignupForm, model: Model): String {
        try {
            signupService.updateProfile(form)
            return "redirect:/signup/photo?id=${form.id}"
        } catch (ex: HttpClientErrorException) {
            loadError(form, ex, model)
            return index(form, model, null)
        }
    }

    private fun loadCategories(model: Model) {
        model.addAttribute(
            "categories",
            categoryService.categories(
                type = CategoryType.USER,
                limit = Integer.MAX_VALUE,
            )
        )
    }

    private fun loadCity(id: Long?, model: Model): LocationModel? {
        val city = id?.let { locationService.location(id) }
            ?: resolveCity()
        val parent = resolveParent(city)

        model.addAttribute("city", city)
        model.addAttribute("cityName",
            city?.let {
                parent?.let { "${city.name}, ${parent.name}" } ?: city.name
            }
        )
        return city
    }
}
