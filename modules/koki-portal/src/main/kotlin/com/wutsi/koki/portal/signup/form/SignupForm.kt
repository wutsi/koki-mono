package com.wutsi.koki.portal.signup.form

data class SignupForm(
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val mobile: String? = null,
    val country: String? = null,
    val cityId: Long? = null,
    val categoryId: Long? = null,
    val employer: String? = null,
    val photoUrl: String = "",
)
