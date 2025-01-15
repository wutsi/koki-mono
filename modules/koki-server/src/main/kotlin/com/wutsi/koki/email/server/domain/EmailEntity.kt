package com.wutsi.koki.email.domain

import com.wutsi.koki.email.dto.RecipientType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_ENTITY")
data class EmailEntity(
    @Id
    val id: String? = null,
    val senderId: Long = -1,
    val recipientId: Long = -1,
    val recipientType: RecipientType = RecipientType.UNKNOWN,
    val subject: String = "",
    val body: String = "",
)
