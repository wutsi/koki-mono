package com.wutsi.koki.message.server.domain

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.MessageStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_MESSAGE")
data class MessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column("owner_fk")
    val ownerId: Long? = null,

    @Column("tenant_fk")
    val tenantId: Long = -1,

    @Column("sender_account_fk")
    val senderAccountId: Long? = null,

    val ownerType: ObjectType? = null,
    val senderName: String = "",
    val senderEmail: String = "",
    val senderPhone: String? = null,
    val body: String = "",
    val createdAt: Date = Date(),
    var status: MessageStatus = MessageStatus.UNKNOWN,
    val country: String? = null,
    val language: String? = null,
)
