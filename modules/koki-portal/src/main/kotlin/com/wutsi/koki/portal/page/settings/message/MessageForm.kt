package com.wutsi.koki.portal.page.settings.message

data class MessageForm(
    val name: String = "",
    val subject: String = "",
    val body: String = "",
    val active: Boolean = true,
)
