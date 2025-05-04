package com.wutsi.koki.room.server.dao

import com.wutsi.koki.room.server.domain.RoomEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomRepository : CrudRepository<RoomEntity, Long>
