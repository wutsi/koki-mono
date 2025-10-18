package com.wutsi.koki.portal.user.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.user.model.ProfileForm
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/users/profile")
class UserProfileController(
    private val categoryService: CategoryService,
) : AbstractPageController() {
    @GetMapping
    fun edit(model: Model): String {
        val form = toProfileForm()
        model.addAttribute("form", form)

        loadLanguages(model)
        loadCountries(model)
        loadCategories(model)

        if (form.cityId != null) {
            val city = locationService.get(form.cityId)
            model.addAttribute("city", city)
        }

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.USER_PROFILE,
                title = userHolder.get()?.displayName ?: ""
            )
        )
        return "/users/profile"
    }

    @PostMapping
    @ResponseBody
    fun submit(@ModelAttribute form: ProfileForm): String {
        return "redirect:${form.backUrl}"
    }

    private fun toProfileForm(): ProfileForm {
        val user = userHolder.get() ?: return ProfileForm()
        return ProfileForm(
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl,
            language = user.language,
            country = user.country,
            cityId = user.city?.id,
            categoryId = user.category?.id,
            employer = user.employer,
            mobile = user.mobile,
            biography = user.biography,
            tiktokUrl = user.tiktokUrl,
            twitterUrl = user.twitterUrl,
            facebookUrl = user.facebookUrl,
            instagramUrl = user.instagramUrl,
            websiteUrl = user.websiteUrl,
            backUrl = request.getHeader(HttpHeaders.REFERER) ?: "/",
        )
    }

    private fun loadCategories(model: Model) {
        model.addAttribute(
            "categories",
            categoryService.search(
                type = CategoryType.USER,
                limit = Integer.MAX_VALUE,
            )
        )
    }
}
