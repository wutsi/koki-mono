package com.wutsi.koki.file.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
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

    @OneToMany()
    @JoinColumn(name = "file_fk")
    val fileOwners: List<FileOwnerEntity> = emptyList(),

    val name: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val url: String = "",
    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
    var description: String? = null,
    var language: String? = null,
    var numberOfPages: Int? = null,
    var data: String? = null,

    @ManyToMany
    @JoinTable(
        name = "T_FILE_LABEL",
        joinColumns = arrayOf(JoinColumn(name = "file_fk")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "label_fk")),
    )
    var labels: List<LabelEntity> = emptyList(),
)
