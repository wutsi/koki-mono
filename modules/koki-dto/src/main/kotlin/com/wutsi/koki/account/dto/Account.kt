package com.wutsi.koki.account.dto

import java.util.Date

data class Account(
    val id: Long = -1,
    val name: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val website: String? = null,
    val language: String? = null,
    val description: String? = null,
    val attributes: Map<Long, String> = emptyMap(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
    val managedById: Long? = null,
)
