package com.wutsi.koki.email.server.domain

import com.wutsi.koki.common.dto.ObjectType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import java.util.Date

@Entity
@Table(name = "T_EMAIL")
data class EmailEntity(
    @Id val id: String? = null,

    @Column(name = "tenant_fk") val tenantId: Long = -1,

    @Column(name = "sender_fk") val senderId: Long = -1,

    @Column(name = "recipient_fk") val recipientId: Long = -1,

    @OneToMany()
    @JoinColumn(name = "email_fk")
    val emailOwners: List<EmailOwnerEntity> = emptyList(),

    @BatchSize(20)
    @OneToMany()
    @JoinColumn(name = "email_fk")
    val attachments: List<AttachmentEntity> = emptyList(),

    val recipientType: ObjectType = ObjectType.UNKNOWN,
    val subject: String = "",
    val body: String = "",
    var summary: String = "",
    val createdAt: Date = Date(),
)
