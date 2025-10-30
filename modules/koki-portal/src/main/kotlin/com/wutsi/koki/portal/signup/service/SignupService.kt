package com.wutsi.koki.portal.signup.service

import com.wutsi.koki.portal.signup.form.SignupForm
import com.wutsi.koki.sdk.KokiUsers
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.UpdateUserPhotoRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class SignupService(
    private val koki: KokiUsers,
) {
    fun create(form: SignupForm): Long {
        return koki.create(
            CreateUserRequest(
                displayName = form.name,
                username = form.username,
                email = form.email,
                password = form.password,
                invitationId = form.invitationId,
                language = LocaleContextHolder.getLocale().language,
            )
        ).userId
    }

    fun updateProfile(form: SignupForm) {
        val user = koki.user(form.id).user
        koki.update(
            user.id,
            UpdateUserRequest(
                displayName = form.name,
                email = form.email,
                employer = form.employer,
                mobile = form.mobileFull,
                categoryId = form.categoryId,
                cityId = form.cityId,
                country = form.country,
                language = form.language,
            )
        )
    }

    fun updatePhoto(form: SignupForm) {
        koki.photo(
            form.id,
            UpdateUserPhotoRequest(photoUrl = form.photoUrl),
        )
    }
}
