package com.wutsi.koki.portal.pub.refdata.service

import com.wutsi.koki.portal.pub.refdata.mapper.AmenityMapper
import com.wutsi.koki.portal.pub.refdata.model.AmenityModel
import com.wutsi.koki.sdk.KokiRefData
import org.springframework.stereotype.Service

@Service
class AmenityService(
    private val koki: KokiRefData,
    private val mapper: AmenityMapper,
) {
    fun amenities(
        ids: List<Long> = emptyList(),
        categoryId: Long? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<AmenityModel> {
        val amenities = koki.amenities(
            ids = ids,
            categoryId = categoryId,
            active = true,
            limit = limit,
            offset = offset
        ).amenities
        return amenities.map { amenity -> mapper.toAmenityModel(amenity) }
    }
}
