package com.wutsi.koki.room.web.room.form

data class SendMessageForm(
    val roomId: Long = -1,
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val body: String = "",
)
