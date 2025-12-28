package com.wutsi.koki.place.server.dao

import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.domain.PlaceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PlaceRepository : CrudRepository<PlaceEntity, Long> {
    fun findByIdAndTenantIdAndDeleted(id: Long, tenantId: Long, deleted: Boolean): Optional<PlaceEntity>

    fun findByAsciiNameIgnoreCaseAndTypeAndCityIdAndTenantIdAndDeleted(
        asciiName: String,
        type: PlaceType,
        cityId: Long,
        tenantId: Long,
        deleted: Boolean,
    ): PlaceEntity?
}
