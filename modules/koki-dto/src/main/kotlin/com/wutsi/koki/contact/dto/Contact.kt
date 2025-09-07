package com.wutsi.koki.contact.dto

import com.wutsi.koki.refdata.dto.Address
import java.util.Date

data class Contact(
    val id: Long = -1,
    val accountId: Long? = null,
    val contactTypeId: Long? = null,
    val salutation: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val gender: Gender = Gender.UNKNOWN,
    val preferredCommunicationMethod: PreferredCommunicationMethod = PreferredCommunicationMethod.UNKNOWN,
    val language: String? = null,
    val profession: String? = null,
    val employer: String? = null,
    val address: Address? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
)
