package com.wutsi.koki.portal.contact.model

import com.wutsi.koki.contact.dto.Gender
import com.wutsi.koki.portal.account.model.AccountModel
import com.wutsi.koki.portal.model.UserModel
import java.util.Date

data class ContactModel(
    val id: Long = -1,
    val account: AccountModel? = null,
    val contactType: ContactTypeModel? = null,
    val salutation: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val gender: Gender = Gender.UNKNOWN,
    val profession: String? = null,
    val employer: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
    val createdBy: UserModel? = null,
    val modifiedBy: UserModel? = null,
) {
    val name: String
        get() = ((salutation ?: "") + " $firstName $lastName").trim()
}
