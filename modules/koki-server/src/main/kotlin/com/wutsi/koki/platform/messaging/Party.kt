package com.wutsi.koki.platform.messaging

data class Party(
    val email: String = "",
    val phoneNumber: String = "",
    val displayName: String? = null,
    val deviceToken: String? = null,
)
