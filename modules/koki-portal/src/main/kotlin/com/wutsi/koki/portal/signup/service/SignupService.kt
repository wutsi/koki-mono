package com.wutsi.koki.portal.signup.service

import com.wutsi.koki.portal.signup.form.SignupForm
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.sdk.KokiUsers
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.SetUserPhotoRequest
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import io.lettuce.core.KillArgs.Builder.id
import org.springframework.stereotype.Service

@Service
class SignupService(
    private val koki: KokiUsers,
    private val configurationService: ConfigurationService
) {
    fun create(form: SignupForm): Long {
        return koki.create(
            CreateUserRequest(
                displayName = form.name,
                username = form.username,
                email = form.email,
                password = form.password,
                roleIds = getRoleId()?.let { id -> listOf(id) } ?: emptyList()
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

                language = user.language,
                roleIds = user.roleIds,
            )
        )
    }

    fun updatePhoto(form: SignupForm) {
        koki.photo(
            form.id,
            SetUserPhotoRequest(photoUrl = form.photoUrl),
        )
    }

    private fun getRoleId(): Long? {
        val configs = configurationService.configurations(listOf(ConfigurationName.PORTAL_SIGNUP_ROLE_ID))
        return configs[ConfigurationName.PORTAL_SIGNUP_ROLE_ID]?.toLong()
    }
}
