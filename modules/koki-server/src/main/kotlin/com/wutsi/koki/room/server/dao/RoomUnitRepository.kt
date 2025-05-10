package com.wutsi.koki.room.server.dao

import com.wutsi.koki.room.server.domain.RoomUnitEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomUnitRepository : CrudRepository<RoomUnitEntity, Long> {
    fun findByNumberAndRoomId(number: String, roomId: Long): RoomUnitEntity?
}
