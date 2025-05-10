package com.wutsi.koki.portal.room.service

import com.wutsi.koki.portal.room.form.RoomUnitForm
import com.wutsi.koki.portal.room.mapper.RoomUnitMapper
import com.wutsi.koki.portal.room.model.RoomUnitModel
import com.wutsi.koki.room.dto.CreateRoomUnitRequest
import com.wutsi.koki.room.dto.RoomUnitStatus
import com.wutsi.koki.room.dto.UpdateRoomUnitRequest
import com.wutsi.koki.sdk.KokiRoomUnits
import org.springframework.stereotype.Service

@Service
class RoomUnitService(
    private val koki: KokiRoomUnits,
    private val mapper: RoomUnitMapper,
) {
    fun roomUnit(id: Long): RoomUnitModel {
        val roomUnit = koki.roomUnit(id).roomUnit
        return mapper.toRoomUnitModel(roomUnit)
    }

    fun roomUnits(
        ids: List<Long> = emptyList(),
        roomId: Long? = null,
        status: RoomUnitStatus? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<RoomUnitModel> {
        val rooms = koki.roomUnits(
            ids = ids,
            roomId = roomId,
            status = status,
            limit = limit,
            offset = offset,
        ).roomUnits

        return rooms.map { roomUnit -> mapper.toRoomUnitModel(roomUnit) }
    }

    fun delete(id: Long) {
        koki.delete(id)
    }

    fun create(form: RoomUnitForm): Long {
        return koki.create(
            CreateRoomUnitRequest(
                roomId = form.roomId,
                floor = form.floor,
                number = form.number,
                status = form.status,
            )
        ).roomUnitId
    }

    fun update(id: Long, form: RoomUnitForm) {
        koki.update(
            id,
            UpdateRoomUnitRequest(
                floor = form.floor,
                number = form.number,
                status = form.status,
            )
        )
    }
}
