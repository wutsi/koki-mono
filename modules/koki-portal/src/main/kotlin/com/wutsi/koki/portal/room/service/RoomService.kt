package com.wutsi.koki.portal.lodging.service

import com.wutsi.koki.lodging.dto.RoomStatus
import com.wutsi.koki.lodging.dto.RoomType
import com.wutsi.koki.portal.lodging.mapper.RoomMapper
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.sdk.KokiRooms
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val koki: KokiRooms,
    private val mapper: RoomMapper,
    private val locationService: LocationService
) {
    fun rooms(
        ids: List<Long> = emptyList(),
        cityId: Long? = null,
        status: RoomStatus? = null,
        type: RoomType? = null,
        totalGuests: Int? = null,
        limit: Int = 20,
        offset: Int =0,
        fullGraph: Boolean = true,
    ) {
        val rooms = koki.rooms(
            ids=ids,
            cityId=cityId,
            status=status,
            type=type,
            totalGuests=totalGuests,
            limit=limit,
            offset=offset,
        )
    }
}
