package com.wutsi.koki.room.web.room.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.room.web.file.service.FileService
import com.wutsi.koki.room.web.refdata.model.AmenityModel
import com.wutsi.koki.room.web.refdata.model.AmenityService
import com.wutsi.koki.room.web.refdata.model.LocationModel
import com.wutsi.koki.room.web.refdata.model.LocationService
import com.wutsi.koki.room.web.room.mapper.RoomMapper
import com.wutsi.koki.room.web.room.model.RoomModel
import com.wutsi.koki.sdk.KokiRooms
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val koki: KokiRooms,
    private val mapper: RoomMapper,
    private val locationService: LocationService,
    private val amenityService: AmenityService,
    private val fileService: FileService,
) {
    fun room(id: Long, fullGraph: Boolean = true): RoomModel {
        val room = koki.room(id).room

        val locationIds =
            listOf(room.address?.cityId, room.address?.stateId, room.neighborhoodId).filterNotNull().distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.locations(
                ids = locationIds, limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        val amenities = if (!fullGraph || room.amenityIds.isEmpty()) {
            emptyMap<Long, AmenityModel>()
        } else {
            amenityService.amenities(
                ids = room.amenityIds, limit = room.amenityIds.size
            ).associateBy { amenity -> amenity.id }
        }

        val images = fileService.files(
            type = FileType.IMAGE,
            ownerId = id,
            ownerType = ObjectType.ROOM,
            limit = 100,
        )

        return mapper.toRoomModel(
            entity = room,
            locations = locations,
            amenities = amenities,
            images = images,
            heroImage = room.heroImageId
                ?.let { id -> images.find { img -> img.id == id } }
                ?: images.firstOrNull()
        )
    }
}
