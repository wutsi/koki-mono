package com.wutsi.koki.portal.pub.user.mapper

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.portal.pub.user.model.UserModel
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import org.springframework.stereotype.Service

@Service
class UserMapper : TenantAwareMapper() {
    fun toUserModel(entity: User, city: LocationModel?): UserModel {
        val fmt = createDateTimeFormat()
        return UserModel(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            emailUrl = entity.email?.let { email -> "mailto:$email" },
            displayName = entity.displayName,
            status = entity.status,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            employer = entity.employer?.ifEmpty { null },
            photoUrl = entity.photoUrl?.ifEmpty { null },
            photoThumbnailUrl = entity.photoThumbnailUrl?.ifEmpty { null },
            photoTinyUrl = entity.photoTinyUrl?.ifEmpty { null },
            photoPreviewUrl = entity.photoPreviewUrl?.ifEmpty { null },
            whatsappUrl = entity.mobile?.let { mobile -> "https://wa.me/" + (mobile.substring(1)) },
            mobile = entity.mobile,
            mobileUrl = entity.mobile?.let { mobile -> "tel:$mobile" },
            mobileText = formatPhone(entity.mobile, city?.country ?: entity.country),
            city = city,
            country = entity.country,
            biography = entity.biography?.ifEmpty { null },
            websiteUrl = entity.websiteUrl?.ifEmpty { null },
            facebookUrl = entity.facebookUrl?.ifEmpty { null },
            instagramUrl = entity.instagramUrl?.ifEmpty { null },
            tiktokUrl = entity.tiktokUrl?.ifEmpty { null },
            youtubeUrl = entity.youtubeUrl?.ifEmpty { null },
            twitterUrl = entity.twitterUrl?.ifEmpty { null },
            slug = slug(entity.displayName),
        )
    }

    fun toUserModel(entity: UserSummary, cities: Map<Long, LocationModel>): UserModel {
        val fmt = createDateTimeFormat()
        val city = entity.cityId?.let { id -> cities[id] }
        return UserModel(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            emailUrl = entity.email?.let { email -> "mailto:$email" },
            displayName = entity.displayName,
            status = entity.status,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            employer = entity.employer?.ifEmpty { null },
            photoUrl = entity.photoUrl?.ifEmpty { null },
            photoThumbnailUrl = entity.photoThumbnailUrl?.ifEmpty { null },
            photoTinyUrl = entity.photoTinyUrl?.ifEmpty { null },
            whatsappUrl = entity.mobile?.let { mobile -> "https://wa.me/" + (mobile.substring(1)) },
            mobile = entity.mobile,
            mobileUrl = entity.mobile?.let { mobile -> "tel:$mobile" },
            mobileText = formatPhone(entity.mobile, city?.country ?: entity.country),
            city = city,
            country = entity.country,
            slug = slug(entity.displayName),
        )
    }

    private fun formatPhone(number: String?, country: String?): String? {
        if (number == null) {
            return null
        } else if (country == null) {
            return number
        }

        try {
            val phoneUtil = PhoneNumberUtil.getInstance()
            val phone = phoneUtil.parse(number, country)
            return if (phoneUtil.isValidNumber(phone)) {
                phoneUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
            } else {
                number
            }
        } catch (ex: Exception) {
            return null
        }
    }

    private fun slug(displayName: String?): String? {
        if (displayName == null) {
            return null
        }
        return StringUtils.toSlug("", displayName).substring(1)
    }
}
