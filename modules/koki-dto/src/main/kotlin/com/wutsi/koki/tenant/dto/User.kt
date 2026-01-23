package com.wutsi.koki.tenant.dto

import java.util.Date

data class User(
    val id: Long = -1,
    val deviceId: String? = null,
    val status: UserStatus = UserStatus.NEW,
    val username: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val language: String? = null,
    val employer: String? = null,
    val mobile: String? = null,
    val invitationId: String? = null,
    val categoryId: Long? = null,
    val street: String? = null,
    val cityId: Long? = null,
    val country: String? = null,
    val photoUrl: String? = null,
    val roleIds: List<Long> = emptyList(),
    val biography: String? = null,
    val websiteUrl: String? = null,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val twitterUrl: String? = null,
    val youtubeUrl: String? = null,
    val tiktokUrl: String? = null,
    val profileStrength: ProfileStrength? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
