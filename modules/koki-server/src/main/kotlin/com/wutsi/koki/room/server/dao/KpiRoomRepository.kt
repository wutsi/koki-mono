package com.wutsi.koki.room.server.dao

import com.wutsi.koki.room.server.domain.KpiRoomEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface KpiRoomRepository : CrudRepository<KpiRoomEntity, Long> {
    fun findByRoomIdAndPeriod(roomId: Long, period: LocalDate): KpiRoomEntity?
}
