package com.wutsi.koki.portal.pub.user.model

import com.wutsi.koki.platform.util.HtmlUtils
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.tenant.dto.UserStatus
import java.util.Date

data class UserModel(
    val id: Long = -1,
    val agentId: Long? = null,
    val username: String = "",
    val email: String? = null,
    val emailUrl: String? = null,
    val displayName: String? = null,
    val employer: String? = null,
    val mobile: String? = null,
    val mobileUrl: String? = null,
    val mobileText: String? = null,
    val photoUrl: String? = null,
    val status: UserStatus = UserStatus.ACTIVE,
    val language: String? = null,
    val languageText: String? = null,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val permissionNames: List<String> = emptyList(),
    val city: LocationModel? = null,
    val country: String? = null,
    val whatsappUrl: String? = null,
    val biography: String? = null,
    val websiteUrl: String? = null,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val twitterUrl: String? = null,
    val tiktokUrl: String? = null,
    val youtubeUrl: String? = null,
    val slug: String? = null,
) {
    val firstName: String
        get() = displayName?.split(" ")?.firstOrNull() ?: ""

    val lastName: String
        get() = displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: ""

    val biographyHtml: String?
        get() = biography?.let { bio -> HtmlUtils.toHtml(bio) }
}
