package com.wutsi.koki.note.server.domain

import com.wutsi.koki.common.dto.ObjectType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_NOTE_OWNER")
data class NoteOwnerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "note_fk")
    val noteId: Long = -1,

    @Column(name = "owner_fk")
    val ownerId: Long = -1,

    val ownerType: ObjectType = ObjectType.UNKNOWN,

    val createdAt: Date = Date()
)
