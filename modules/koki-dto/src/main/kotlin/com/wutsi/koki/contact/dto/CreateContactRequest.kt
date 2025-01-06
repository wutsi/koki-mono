package com.wutsi.koki.contact.dto

import jakarta.validation.constraints.NotEmpty

data class CreateContactRequest(
    val accountId: Long? = null,
    val contactTypeId: Long? = null,
    val salutations: String? = null,
    @get:NotEmpty() val firstName: String = "",
    @get:NotEmpty() val lastName: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val gender: Gender = Gender.UNKNOWN,
    val profession: String? = null,
    val employer: String? = null,
)
