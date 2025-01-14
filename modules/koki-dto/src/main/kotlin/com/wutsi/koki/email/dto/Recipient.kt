package com.wutsi.koki.email.dto

data class Recipient(
    val id: Long = -1,
    val type: RecipientType = RecipientType.ACCOUNT
)
