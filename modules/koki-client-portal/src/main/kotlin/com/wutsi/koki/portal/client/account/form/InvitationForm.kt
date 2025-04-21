package com.wutsi.koki.portal.client.account.form

data class InvitationForm(
    val accountId: Long = -1,
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirm: String = "",
)
