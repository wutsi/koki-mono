package com.wutsi.koki.file.server.domain

import com.wutsi.koki.common.dto.ObjectType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_FILE_OWNER")
data class FileOwnerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "file_fk")
    val fileId: Long = -1,

    @Column(name = "owner_fk")
    val ownerId: Long = -1,

    val ownerType: ObjectType = ObjectType.UNKNOWN,
)
