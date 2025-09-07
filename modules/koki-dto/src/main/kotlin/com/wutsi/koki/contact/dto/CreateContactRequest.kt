package com.wutsi.koki.contact.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateContactRequest(
    val accountId: Long? = null,
    val contactTypeId: Long? = null,

    @get:Size(max = 10) val salutations: String? = null,
    @get:NotEmpty() @get:Size(max = 100) val firstName: String = "",
    @get:NotEmpty() @get:Size(max = 100) val lastName: String = "",
    @get:Size(max = 30) val phone: String? = null,
    @get:Size(max = 30) val mobile: String? = null,
    @get:Size(max = 255) val email: String? = null,
    @get:Size(max = 100) val profession: String? = null,
    @get:Size(max = 100) val employer: String? = null,

    val gender: Gender = Gender.UNKNOWN,
    val preferredCommunicationMethod: PreferredCommunicationMethod = PreferredCommunicationMethod.UNKNOWN,
    @get:Size(max = 2) val language: String? = null,
)
