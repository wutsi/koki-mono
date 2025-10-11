package com.wutsi.koki.portal.pub.user.mapper

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.portal.pub.user.model.UserModel
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.UserSummary
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class UserMapper : TenantAwareMapper() {
    fun toUserModel(
        entity: User,
        city: LocationModel?,
    ): UserModel {
        val fmt = createDateTimeFormat()
        return UserModel(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
            language = entity.language,
            languageText = entity.language?.let { lang -> Locale(lang).displayName },
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            employer = entity.employer,
            photoUrl = entity.photoUrl,
            mobile = entity.mobile,
            whatsappUrl = entity.mobile?.let { mobile -> "https://wa.me/" + (mobile.substring(1)) },
            mobileText = formatPhone(entity.mobile, city?.country ?: entity.country),
            city = city,
        )
    }

    fun toUserModel(entity: UserSummary, cities: Map<Long, LocationModel>): UserModel {
        val fmt = createDateTimeFormat()
        val city = entity.cityId?.let { id -> cities[id] }
        return UserModel(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            displayName = entity.displayName,
            status = entity.status,
            createdAt = entity.createdAt,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAt = entity.modifiedAt,
            modifiedAtText = fmt.format(entity.modifiedAt),
            employer = entity.employer,
            photoUrl = entity.photoUrl,
            mobile = entity.mobile,
            whatsappUrl = entity.mobile?.let { mobile -> "https://wa.me/" + (mobile.substring(1)) },
            mobileText = formatPhone(entity.mobile, city?.country ?: entity.country),
            city = city,
            country = entity.country,
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
}
