package com.wutsi.koki.form.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ACCOUNT_ATTRIBUTE")
data class AccountAttributeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "account_fk")
    val accountId: Long = -1,

    @Column(name = "attribute_fk")
    val attributeId: Long = -1,

    var value: String? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
