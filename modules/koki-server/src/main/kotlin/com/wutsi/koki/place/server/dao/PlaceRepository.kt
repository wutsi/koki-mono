package com.wutsi.koki.place.server.dao

import com.wutsi.koki.place.server.domain.PlaceEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PlaceRepository : CrudRepository<PlaceEntity, Long> {
    fun findByIdAndTenantIdAndDeleted(id: Long, tenantId: Long, deleted: Boolean): Optional<PlaceEntity>

    fun findByTenantIdAndDeletedAtIsNull(tenantId: Long, pageable: Pageable): List<PlaceEntity>
}
