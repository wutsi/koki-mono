package com.wutsi.koki.portal.contact.form

import com.wutsi.koki.contact.dto.Gender
import com.wutsi.koki.contact.dto.PreferredCommunicationMethod

data class ContactForm(
    val accountId: Long = -1,
    val contactTypeId: Long = -1,
    val salutation: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val phoneFull: String? = null,
    val mobileFull: String? = null,
    val email: String? = null,
    val gender: Gender = Gender.UNKNOWN,
    val profession: String? = null,
    val employer: String? = null,
    val language: String? = null,
    val preferredCommunicationMethod: PreferredCommunicationMethod = PreferredCommunicationMethod.UNKNOWN,
    val country: String? = null,
    val cityId: Long? = null,
    val street: String? = null,
    val postalCode: String? = null,
)
