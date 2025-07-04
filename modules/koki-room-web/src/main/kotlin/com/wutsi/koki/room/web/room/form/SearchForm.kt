package com.wutsi.koki.room.web.room.form

data class SearchForm(
    val roomType: String? = null,
    val leaseType: String? = null,
    val furnishedType: String? = null,
    val bedrooms: Int? = null,
)
