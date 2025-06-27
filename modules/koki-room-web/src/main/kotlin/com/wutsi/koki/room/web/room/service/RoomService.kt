package com.wutsi.koki.room.web.room.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.web.account.model.AccountModel
import com.wutsi.koki.room.web.account.service.AccountService
import com.wutsi.koki.room.web.file.model.FileModel
import com.wutsi.koki.room.web.file.service.FileService
import com.wutsi.koki.room.web.refdata.model.AmenityModel
import com.wutsi.koki.room.web.refdata.model.LocationModel
import com.wutsi.koki.room.web.refdata.service.AmenityService
import com.wutsi.koki.room.web.refdata.service.LocationService
import com.wutsi.koki.room.web.room.mapper.RoomMapper
import com.wutsi.koki.room.web.room.model.MapMarkerModel
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
    private val accountService: AccountService,
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

        val account = accountService.account(room.accountId)

        val images = fileService.files(
            type = FileType.IMAGE,
            ownerId = id,
            ownerType = ObjectType.ROOM,
            limit = 100,
        )

        return mapper.toRoomModel(
            entity = room,
            account = account,
            locations = locations,
            amenities = amenities,
            images = images,
            heroImage = room.heroImageId
                ?.let { id -> images.find { img -> img.id == id } }
                ?: images.firstOrNull()
        )
    }

    fun rooms(
        ids: List<Long> = emptyList(),
        accountIds: List<Long> = emptyList(),
        cityId: Long? = null,
        neighborhoodId: Long? = null,
        types: List<RoomType> = emptyList(),
        minBudget: Double? = null,
        maxBudget: Double? = null,
        leaseType: LeaseType? = null,
        furnishedType: FurnishedType? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<RoomModel> {
        val rooms = koki.rooms(
            ids = ids,
            accountIds = accountIds,
            cityId = cityId,
            status = RoomStatus.PUBLISHED,
            types = types,
            maxRooms = null,
            minRooms = null,
            maxBathrooms = null,
            minBathrooms = null,
            amenityIds = emptyList(),
            neighborhoodId = neighborhoodId,
            categoryIds = emptyList(),
            totalGuests = null,
            accountManagerIds = emptyList(),
            minBudget = minBudget,
            maxBudget = maxBudget,
            leaseType = leaseType,
            furnishedType = furnishedType,
            limit = limit,
            offset = offset,
        ).rooms

        val locationIds = rooms
            .flatMap { room -> listOf(room.address?.cityId, room.address?.stateId, room.neighborhoodId) }
            .filterNotNull()
            .distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.locations(
                ids = locationIds, limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        val imageIds = rooms
            .map { room -> room.heroImageId }
            .filterNotNull()
            .distinct()
        val images = if (!fullGraph || imageIds.isEmpty()) {
            emptyMap<Long, FileModel>()
        } else {
            fileService.files(
                ids = imageIds,
                type = FileType.IMAGE,
                limit = imageIds.size
            ).associateBy { image -> image.id }
        }

        val accountIds = rooms.map { room -> room.accountId }.distinct()
        val accounts = if (!fullGraph || accountIds.isEmpty()) {
            emptyMap<Long, AccountModel>()
        } else {
            accountService.accounts(
                ids = accountIds,
                limit = accountIds.size
            ).associateBy { account -> account.id }
        }

        return rooms.map { room ->
            mapper.toRoomModel(
                entity = room,
                accounts = accounts,
                locations = locations,
                images = images,
            )
        }
    }

    fun map(
        ids: List<Long> = emptyList(),
        accountIds: List<Long> = emptyList(),
        cityId: Long? = null,
        neighborhoodId: Long? = null,
        types: List<RoomType> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<MapMarkerModel> {
        val rooms = koki.rooms(
            ids = ids,
            accountIds = accountIds,
            cityId = cityId,
            status = RoomStatus.PUBLISHED,
            types = types,
            maxRooms = null,
            minRooms = null,
            maxBathrooms = null,
            minBathrooms = null,
            amenityIds = emptyList(),
            neighborhoodId = neighborhoodId,
            categoryIds = emptyList(),
            accountManagerIds = emptyList(),
            totalGuests = null,
            limit = limit,
            offset = offset,
        ).rooms
        return rooms
            .filter { room -> room.latitude != null && room.longitude != null }
            .map { room -> mapper.toMapMarkerModel(room) }
            .filter { room -> room.price != null }
    }
}
