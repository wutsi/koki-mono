package com.wutsi.koki.room.server.dao

import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.room.server.domain.RoomLocationMetricEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoomLocationMetricRepository : CrudRepository<RoomLocationMetricEntity, Long> {
    fun findByTenantIdAndLocation(tenantId: Long, location: LocationEntity): RoomLocationMetricEntity?
}
