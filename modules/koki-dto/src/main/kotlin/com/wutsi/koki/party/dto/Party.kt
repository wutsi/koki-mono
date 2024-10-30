package com.wutsi.koki.party.dto

import java.util.Date

data class Party(
    val id: Long = -1,
    val userId: Long = -1,
    val name: String = "",
    val language: String? = null,
    val phoneNumber: String? = null,
    val type: PartyType = PartyType.UNKNOWN,
    val createdAt: Date = Date(),
)
