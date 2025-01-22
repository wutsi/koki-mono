package com.wutsi.koki.email.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_ATTACHMENT")
data class AttachmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "email_fk")
    val emailId: String = "",

    @Column(name = "file_fk")
    val fileId: Long = -1,
)
