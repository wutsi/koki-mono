package com.wutsi.koki.note.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.note.dto.CreateNoteRequest
import com.wutsi.koki.note.dto.UpdateNoteRequest
import com.wutsi.koki.note.server.dao.NoteOwnerRepository
import com.wutsi.koki.note.server.dao.NoteRepository
import com.wutsi.koki.note.server.domain.NoteEntity
import com.wutsi.koki.note.server.domain.NoteOwnerEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.util.Date

@Service
class NoteService(
    private val dao: NoteRepository,
    private val ownerDao: NoteOwnerRepository,
    private val securityService: SecurityService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): NoteEntity {
        val note = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.NOTE_NOT_FOUND)) }

        if (note.tenantId != tenantId || note.deleted) {
            throw NotFoundException(Error(ErrorCode.NOTE_NOT_FOUND))
        }
        return note
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<NoteEntity> {
        val jql = StringBuilder("SELECT F FROM NoteEntity AS F")
        if (ownerId != null || ownerType != null) {
            jql.append(" JOIN F.noteOwners AS O")
        }

        jql.append(" WHERE F.deleted=false AND F.tenantId=:tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND F.id IN :ids")
        }
        if (ownerId != null) {
            jql.append(" AND O.ownerId = :ownerId")
        }
        if (ownerType != null) {
            jql.append(" AND O.ownerType = :ownerType")
        }
        jql.append(" ORDER BY F.modifiedAt DESC")

        val query = em.createQuery(jql.toString(), NoteEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (ownerId != null) {
            query.setParameter("ownerId", ownerId)
        }
        if (ownerType != null) {
            query.setParameter("ownerType", ownerType)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateNoteRequest, tenantId: Long): NoteEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        val now = Date()
        val note = dao.save(
            NoteEntity(
                tenantId = tenantId,
                subject = request.subject,
                body = request.body,
                summary = toSummary(request.body),
                type = request.type,
                duration = request.duration,
                createdById = userId,
                modifiedById = userId,
                createdAt = now,
                modifiedAt = now,
            )
        )

        if (request.owner != null) {
            val ref = request.owner!!
            ownerDao.save(
                NoteOwnerEntity(
                    noteId = note.id!!,
                    ownerId = ref.id,
                    ownerType = ref.type,
                    createdAt = now,
                )
            )
        }
        return note
    }

    @Transactional
    fun update(id: Long, request: UpdateNoteRequest, tenantId: Long) {
        val note = get(id, tenantId)
        note.subject = request.subject
        note.body = request.body
        note.type = request.type
        note.summary = toSummary(request.body)
        note.duration = request.duration
        note.modifiedAt = Date()
        note.modifiedById = securityService.getCurrentUserIdOrNull()
        dao.save(note)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val note = get(id, tenantId)
        note.deleted = true
        note.deletedAt = Date()
        note.deletedById = securityService.getCurrentUserIdOrNull()
        dao.save(note)
    }

    private fun toSummary(html: String): String {
        val text = Jsoup.parse(html).text()
        return if (text.length > 255) {
            text.take(252) + "..."
        } else {
            text
        }
    }
}
