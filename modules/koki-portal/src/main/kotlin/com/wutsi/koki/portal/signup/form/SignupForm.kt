package com.wutsi.koki.portal.signup.form

data class SignupForm(
    val id: Long = -1,
    val name: String? = null,
    val email: String? = null,
    val username: String = "",
    val password: String = "",
    val mobile: String? = null,
    val mobileFull: String? = null,
    val country: String? = null,
    val cityId: Long? = null,
    val categoryId: Long? = null,
    val employer: String? = null,
    val photoUrl: String? = null,
)
