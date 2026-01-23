package com.wutsi.koki.portal.user.model

data class ProfileForm(
    val displayName: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val mobileFull: String? = null,
    val country: String? = null,
    val cityId: Long? = null,
    val street: String? = null,
    val categoryId: Long? = null,
    val employer: String? = null,
    val photoUrl: String? = null,
    val language: String? = null,
    val biography: String? = null,
    val websiteUrl: String? = null,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val twitterUrl: String? = null,
    val tiktokUrl: String? = null,
    val youtubeUrl: String? = null,
    val backUrl: String = "",
)
