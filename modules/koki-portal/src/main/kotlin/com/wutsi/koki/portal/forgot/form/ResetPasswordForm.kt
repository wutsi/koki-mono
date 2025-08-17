package com.wutsi.koki.portal.forgot.form

data class ResetPasswordForm(
    val tokenId: String = "",
    val password: String = "",
)
