package com.wutsi.koki.note.server.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

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

    val ownerType: String = "",
)
