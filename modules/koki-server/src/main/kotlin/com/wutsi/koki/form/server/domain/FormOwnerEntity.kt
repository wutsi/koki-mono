package com.wutsi.koki.form.server.domain

import com.wutsi.koki.common.dto.ObjectType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_FORM_OWNER")
data class FormOwnerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "form_fk")
    val formId: Long = -1,

    @Column(name = "owner_fk")
    val ownerId: Long = -1,

    val ownerType: ObjectType = ObjectType.UNKNOWN,
)
