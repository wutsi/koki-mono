package com.wutsi.koki.room.server.dao

import com.wutsi.koki.room.server.domain.KpiRoomEntity
import com.wutsi.koki.room.server.domain.RoomEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.Date

@Repository
interface KpiRoomRepository : CrudRepository<KpiRoomEntity, Long>{
    fun findByRoomIdAndPeriod(roomId: Long, period: LocalDate): KpiRoomEntity?
}
