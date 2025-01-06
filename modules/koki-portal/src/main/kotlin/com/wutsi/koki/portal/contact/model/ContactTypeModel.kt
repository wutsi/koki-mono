package com.wutsi.koki.portal.contact.model

data class ContactTypeModel(
    val id: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val active: Boolean = false,
)
