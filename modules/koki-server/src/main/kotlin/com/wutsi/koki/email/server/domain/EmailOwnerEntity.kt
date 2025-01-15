package com.wutsi.koki.email.server.domain

import com.wutsi.koki.common.dto.ObjectType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_EMAIL_OWNER")
data class EmailOwnerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "email_fk")
    val emailId: String = "",

    @Column(name = "owner_fk")
    val ownerId: Long = -1,

    val ownerType: ObjectType = ObjectType.UNKNOWN,
)
