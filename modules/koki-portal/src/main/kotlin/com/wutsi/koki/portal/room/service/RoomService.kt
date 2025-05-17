package com.wutsi.koki.portal.room.service

import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.refdata.service.AmenityService
import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.portal.room.form.GeoLocationForm
import com.wutsi.koki.portal.room.form.RoomForm
import com.wutsi.koki.portal.room.mapper.RoomMapper
import com.wutsi.koki.portal.room.model.RoomModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.room.dto.AddAmenityRequest
import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.SaveRoomGeoLocationRequest
import com.wutsi.koki.room.dto.UpdateRoomRequest
import com.wutsi.koki.sdk.KokiRooms
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val koki: KokiRooms,
    private val mapper: RoomMapper,
    private val locationService: LocationService,
    private val userService: UserService,
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

        val userIds = listOf(room.createdById, room.modifiedById, room.publishedById).filterNotNull().distinct()
        val users = if (!fullGraph || userIds.isEmpty()) {
            emptyMap<Long, UserModel>()
        } else {
            userService.users(
                ids = userIds, limit = userIds.size
            ).associateBy { user -> user.id }
        }

        val amenities = if (!fullGraph || room.amenityIds.isEmpty()) {
            emptyMap<Long, AmenityModel>()
        } else {
            amenityService.amenities(
                ids = room.amenityIds, limit = room.amenityIds.size
            ).associateBy { amenity -> amenity.id }
        }

        val image = if (!fullGraph || room.heroImageId == null) {
            null
        } else {
            fileService.file(room.heroImageId!!)
        }

        return mapper.toRoomModel(
            entity = room,
            locations = locations,
            users = users,
            amenities = amenities,
            image = image,
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

        val locationIds =
            rooms.flatMap { room -> listOf(room.address?.cityId, room.address?.stateId, room.neighborhoodId) }
                .filterNotNull().distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.locations(
                ids = locationIds, limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        val imageIds = rooms.map { room -> room.heroImageId }.filterNotNull().distinct()
        val images = if (!fullGraph || imageIds.isEmpty()) {
            emptyMap<Long, FileModel>()
        } else {
            fileService.files(
                ids = imageIds, type = FileType.IMAGE, limit = locationIds.size
            ).associateBy { image -> image.id }
        }

        return rooms.map { room ->
            mapper.toRoomModel(
                entity = room,
                locations = locations,
                images = images,
            )
        }
    }

    fun create(form: RoomForm): Long {
        return koki.create(
            request = CreateRoomRequest(
                type = form.type,
                title = form.title,
                description = form.description?.ifEmpty { null },
                numberOfRooms = form.numberOfRooms,
                numberOfBeds = form.numberOfBeds,
                numberOfBathrooms = form.numberOfBathrooms,
                maxGuests = form.maxGuests,
                currency = form.currency,
                pricePerNight = form.pricePerNight,
                cityId = form.cityId,
                postalCode = form.postalCode,
                street = form.street,
                checkoutTime = form.checkoutTime?.ifEmpty { null },
                checkinTime = form.checkinTime?.ifEmpty { null },
                neighborhoodId = form.neighborhoodId,
            )
        ).roomId
    }

    fun update(id: Long, form: RoomForm) {
        koki.update(
            id = id, request = UpdateRoomRequest(
                type = form.type,
                title = form.title,
                description = form.description?.ifEmpty { null },
                numberOfRooms = form.numberOfRooms,
                numberOfBeds = form.numberOfBeds,
                numberOfBathrooms = form.numberOfBathrooms,
                maxGuests = form.maxGuests,
                currency = form.currency,
                pricePerNight = form.pricePerNight,
                cityId = form.cityId,
                postalCode = form.postalCode,
                street = form.street,
                checkoutTime = form.checkoutTime?.ifEmpty { null },
                checkinTime = form.checkinTime?.ifEmpty { null },
                neighborhoodId = form.neighborhoodId,
            )
        )
    }

    fun delete(id: Long) {
        koki.delete(id)
    }

    fun addAmenity(roomId: Long, amenityId: Long) {
        koki.addAmenities(
            roomId, AddAmenityRequest(amenityIds = listOf(amenityId))
        )
    }

    fun removeAmenity(roomId: Long, amenityId: Long) {
        koki.removeAmenity(roomId, amenityId)
    }

    fun publish(roomId: Long) {
        koki.publish(roomId)
    }

    fun save(roomId: Long, form: GeoLocationForm) {
        koki.saveGeolocation(
            roomId,
            SaveRoomGeoLocationRequest(
                longitude = form.longitude!!,
                latitude = form.latitude!!,
            )
        )
    }
}
