package com.wutsi.koki.portal.room.service

import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.room.form.RoomForm
import com.wutsi.koki.portal.room.mapper.RoomMapper
import com.wutsi.koki.portal.room.model.RoomModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.UpdateRoomRequest
import com.wutsi.koki.sdk.KokiRooms
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val koki: KokiRooms,
    private val mapper: RoomMapper,
    private val locationService: LocationService,
    private val userService: UserService,
) {
    fun room(id: Long, fullGraph: Boolean = true): RoomModel {
        val room = koki.room(id).room

        val locationIds = listOf(room.address.cityId, room.address.stateId)
            .filterNotNull()
            .distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.locations(
                ids = locationIds,
                limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        val userIds = listOf(room.createdById, room.modifiedById)
            .filterNotNull()
            .distinct()
        val users = if (!fullGraph || userIds.isEmpty()) {
            emptyMap<Long, UserModel>()
        } else {
            userService.users(
                ids = userIds,
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        return mapper.toRoomModel(
            entity = room,
            locations = locations,
            users = users,
        )
    }

    fun rooms(
        ids: List<Long> = emptyList(),
        cityId: Long? = null,
        status: RoomStatus? = null,
        type: RoomType? = null,
        totalGuests: Int? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<RoomModel> {
        val rooms = koki.rooms(
            ids = ids,
            cityId = cityId,
            status = status,
            type = type,
            totalGuests = totalGuests,
            limit = limit,
            offset = offset,
        ).rooms

        val locationIds = rooms.flatMap { room -> listOf(room.address.cityId, room.address.stateId) }
            .filterNotNull()
            .distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.locations(
                ids = locationIds,
                limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        return rooms.map { room ->
            mapper.toRoomModel(
                entity = room,
                locations = locations
            )
        }
    }

    fun create(form: RoomForm): Long {
        return koki.create(
            request = CreateRoomRequest(
                type = form.type,
                title = form.title,
                description = form.description,
                numberOfRooms = form.numberOfRooms,
                numberOfBeds = form.numberOfBeds,
                numberOfBathrooms = form.numberOfBathrooms,
                maxGuests = form.maxGuests,
                currency = form.currency,
                pricePerNight = form.pricePerNight,
                cityId = form.cityId ?: -1,
                postalCode = form.postalCode,
                street = form.street,
            )
        ).roomId
    }

    fun update(id: Long, form: RoomForm) {
        koki.update(
            id = id,
            request = UpdateRoomRequest(
                type = form.type,
                title = form.title,
                description = form.description,
                numberOfRooms = form.numberOfRooms,
                numberOfBeds = form.numberOfBeds,
                numberOfBathrooms = form.numberOfBathrooms,
                maxGuests = form.maxGuests,
                currency = form.currency,
                pricePerNight = form.pricePerNight,
                cityId = form.cityId ?: -1,
                postalCode = form.postalCode,
                street = form.street,
            )
        )
    }

    fun delete(id: Long) {
        koki.delete(id)
    }
}
