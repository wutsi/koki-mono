package com.wutsi.koki.note.server.domain

import com.wutsi.koki.note.dto.NoteType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_NOTE")
data class NoteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "tenant_fk")
    val tenantId: Long = -1,

    @Column(name = "created_by_fk")
    val createdById: Long? = null,

    @Column(name = "modified_by_fk")
    var modifiedById: Long? = null,

    @Column(name = "deleted_by_fk")
    var deletedById: Long? = null,

    @OneToMany()
    @JoinColumn(name = "note_fk")
    val noteOwners: List<NoteOwnerEntity> = emptyList(),

    var subject: String = "",
    var body: String = "",
    var summary: String = "",
    var type: NoteType = NoteType.UNKNOWN,
    var duration: Int = 0,

    var deleted: Boolean = false,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var deletedAt: Date? = null,
)
