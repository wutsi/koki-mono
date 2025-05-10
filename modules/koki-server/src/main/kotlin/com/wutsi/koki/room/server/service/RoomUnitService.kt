package com.wutsi.koki.room.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.room.dto.CreateRoomUnitRequest
import com.wutsi.koki.room.dto.RoomUnitStatus
import com.wutsi.koki.room.dto.UpdateRoomUnitRequest
import com.wutsi.koki.room.server.dao.RoomUnitRepository
import com.wutsi.koki.room.server.domain.RoomUnitEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class RoomUnitService(
    private val dao: RoomUnitRepository,
    private val em: EntityManager,
    private val securityService: SecurityService,
) {
    fun get(id: Long, tenantId: Long): RoomUnitEntity {
        val roomUnit = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.ROOM_UNIT_NOT_FOUND)) }

        if (roomUnit.tenantId != tenantId || roomUnit.deleted) {
            throw NotFoundException(Error(ErrorCode.ROOM_UNIT_NOT_FOUND))
        }
        return roomUnit
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        roomId: Long? = null,
        status: RoomUnitStatus? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<RoomUnitEntity> {
        val jql = StringBuilder("SELECT A FROM RoomUnitEntity A WHERE A.deleted=false AND A.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND A.id IN :ids")
        }
        if (roomId != null) {
            jql.append(" AND A.roomId IN :roomId")
        }
        if (status != null) {
            jql.append(" AND A.status IN :status")
        }
        jql.append(" ORDER BY A.number")

        val query = em.createQuery(jql.toString(), RoomUnitEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (roomId != null) {
            query.setParameter("roomId", roomId)
        }
        if (status != null) {
            query.setParameter("status", status)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateRoomUnitRequest, tenantId: Long): RoomUnitEntity {
        checkDuplicateNumber(null, request.number, request.roomId)

        val now = Date()
        val userId = securityService.getCurrentUserIdOrNull()
        return dao.save(
            RoomUnitEntity(
                tenantId = tenantId,
                roomId = request.roomId,
                status = request.status,
                floor = request.floor,
                number = request.number,
                createdById = userId,
                modifiedById = userId,
                createdAt = now,
                modifiedAt = now,
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateRoomUnitRequest, tenantId: Long) {
        val roomUnit = get(id, tenantId)
        checkDuplicateNumber(id, request.number, roomUnit.roomId)

        roomUnit.status = request.status
        roomUnit.floor = request.floor
        roomUnit.number = request.number
        roomUnit.modifiedById = securityService.getCurrentUserIdOrNull()
        roomUnit.modifiedAt = Date()
        dao.save(roomUnit)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val roomUnit = get(id, tenantId)
        if (!roomUnit.deleted) {
            roomUnit.deleted = true
            roomUnit.deletedAt = Date()
            roomUnit.deleteById = securityService.getCurrentUserIdOrNull()
            dao.save(roomUnit)
        }
    }

    private fun checkDuplicateNumber(id: Long?, number: String, roomId: Long) {
        val roomUnit = dao.findByNumberAndRoomId(number, roomId)
        if (roomUnit?.id != null && roomUnit.id != id) {
            throw ConflictException(
                error = Error(code = ErrorCode.ROOM_UNIT_DUPLICATE_NUMBER)
            )
        }
    }
}
