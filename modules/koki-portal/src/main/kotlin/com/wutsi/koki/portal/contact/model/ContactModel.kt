package com.wutsi.koki.portal.contact.model

import com.wutsi.koki.contact.dto.Gender
import com.wutsi.koki.contact.dto.PreferredCommunicationMethod
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.portal.user.model.UserModel
import java.util.Date

data class ContactModel(
    val id: Long = -1,
    val account: AccountModel? = null,
    val contactType: TypeModel? = null,
    val salutation: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val phoneFormatted: String? = null,
    val mobileFormatted: String? = null,
    val email: String? = null,
    val gender: Gender = Gender.UNKNOWN,
    val language: String? = null,
    val languageText: String? = null,
    val profession: String? = null,
    val employer: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null,
    val readOnly: Boolean = false,
    val preferredCommunicationMethod: PreferredCommunicationMethod = PreferredCommunicationMethod.UNKNOWN,
    val address: AddressModel? = null,
) {
    val name: String
        get() = ((salutation ?: "") + " $firstName $lastName").trim()

    val phoneUrl: String?
        get() = phone?.let { "tel:$phone" }

    val mobileUrl: String?
        get() = mobile?.let {
            when (preferredCommunicationMethod) {
                PreferredCommunicationMethod.WHATSAPP -> "https://wa.me/" + (mobile.substring(1))
                else -> "tel:$phone"
            }
        }

    val emailUrl: String?
        get() = email?.let { "mailto:$email" }

    fun canAccess(user: UserModel?): Boolean {
        return user != null &&
            (user.hasFullAccess("contact") || (user.canAccess("contact") && createdBy?.id == user.id))
    }

    fun canManage(user: UserModel?): Boolean {
        return user != null &&
            (user.hasFullAccess("contact") || (user.canManage("contact") && createdBy?.id == user.id))
    }

    fun canDelete(user: UserModel?): Boolean {
        return user != null &&
            (user.hasFullAccess("contact") || (user.canDelete("contact") && createdBy?.id == user.id))
    }
}
