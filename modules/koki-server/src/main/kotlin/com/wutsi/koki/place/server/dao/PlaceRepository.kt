package com.wutsi.koki.place.server.dao

import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.domain.PlaceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PlaceRepository : CrudRepository<PlaceEntity, Long> {
    fun findByIdAndDeleted(id: Long, deleted: Boolean): Optional<PlaceEntity>

    fun findByAsciiNameIgnoreCaseAndTypeAndCityIdAndDeleted(
        asciiName: String,
        type: PlaceType,
        cityId: Long,
        deleted: Boolean,
    ): PlaceEntity?
}
