package com.wutsi.koki.portal.pub.refdata.service

import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.portal.pub.refdata.mapper.LocationMapper
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.sdk.KokiRefData
import org.springframework.stereotype.Service

@Service
class LocationService(
    private val koki: KokiRefData,
    private val mapper: LocationMapper,
) {
    fun locations(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        parentId: Long? = null,
        types: List<LocationType> = emptyList(),
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<LocationModel> {
        val locations = koki.locations(
            keyword = keyword,
            ids = ids,
            parentId = parentId,
            types = types,
            country = country,
            limit = limit,
            offset = offset
        ).locations
        return locations.map { location -> mapper.toLocationModel(location) }
    }

    fun location(id: Long): LocationModel {
        val location = koki.location(id).location
        return mapper.toLocationModel(location)
    }
}
