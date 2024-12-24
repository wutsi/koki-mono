package com.wutsi.koki.portal.model

data class RecipientModel(
    val displayName: String? = null,
    val email: String = "",
) {
    val emailAddress: String
        get() = if (displayName.isNullOrEmpty()) {
            email
        } else {
            "$displayName <$email>"
        }
}
