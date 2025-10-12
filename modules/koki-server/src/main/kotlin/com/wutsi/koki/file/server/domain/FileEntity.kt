package com.wutsi.koki.file.server.domain

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.ImageQuality
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_FILE")
data class FileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    var createdById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    @Column(name = "owner_fk")
    val ownerId: Long? = null,

    val ownerType: ObjectType? = null,
    val type: FileType = FileType.UNKNOWN,
    val name: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val url: String = "",
    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
    var title: String? = null,
    var description: String? = null,
    var language: String? = null,
    var numberOfPages: Int? = null,
    var status: FileStatus = FileStatus.UNKNOWN,
    var rejectionReason: String? = null,
    var titleFr: String? = null,
    var descriptionFr: String? = null,
    var width: Int? = null,
    var height: Int? = null,
    var imageQuality: ImageQuality? = null,
)
