package com.wutsi.koki.portal.reference.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.room.service.RoomService
import org.springframework.stereotype.Service

@Service
class ObjectReferenceService(
    private val roomService: RoomService,
) {
    fun reference(id: Long, type: ObjectType): ObjectReferenceModel {
        if (type == ObjectType.ROOM) {
            return roomService.room(id).toObjectReference()
        } else {
            return ObjectReferenceModel(id = id, type = type)
        }
    }

    fun references(ids: List<Long>, type: ObjectType): List<ObjectReferenceModel> {
        if (type == ObjectType.ROOM) {
            val rooms = roomService.rooms(ids = ids, limit = ids.size)
            return rooms.map { room -> room.toObjectReference() }
        } else {
            return ids.map { id -> ObjectReferenceModel(id = id, type = type) }
        }
    }
}
