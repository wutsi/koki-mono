package com.wutsi.koki.portal.page.message

data class MessageForm(
    val name: String = "",
    val subject: String = "",
    val body: String = "",
    val active: Boolean = true,
)
