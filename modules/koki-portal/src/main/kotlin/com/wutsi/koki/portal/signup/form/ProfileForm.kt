package com.wutsi.koki.portal.signup.form

data class ProfileForm(
    val name: String? = null,
    val email: String? = null,
    val whatsapp: String? = null,
    val country: String? = null,
    val cityId: Long? = null,
)
