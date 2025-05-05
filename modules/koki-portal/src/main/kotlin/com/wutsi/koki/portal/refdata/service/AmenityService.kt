package com.wutsi.koki.portal.refdata.service

import com.wutsi.koki.portal.refdata.mapper.RefDataMapper
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.sdk.KokiRefData
import org.springframework.stereotype.Service

@Service
class AmenityService(
    private val koki: KokiRefData,
    private val mapper: RefDataMapper,
) {
    fun amenities(
        ids: List<Long> = emptyList(),
        categoryId: Long? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<AmenityModel> {
        val amenities = koki.amenities(
            ids = ids,
            categoryId = categoryId,
            active = active,
            limit = limit,
            offset = offset
        ).amenities
        return amenities.map { amenity -> mapper.toAmenityModel(amenity) }
    }
}
