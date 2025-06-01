package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.room.dto.AddAmenityRequest
import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.CreateRoomResponse
import com.wutsi.koki.room.dto.GetRoomResponse
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.SaveRoomGeoLocationRequest
import com.wutsi.koki.room.dto.SearchRoomResponse
import com.wutsi.koki.room.dto.SetHeroImageRequest
import com.wutsi.koki.room.dto.UpdateRoomRequest
import com.wutsi.koki.room.server.command.PublishRoomCommand
import com.wutsi.koki.room.server.mapper.RoomMapper
import com.wutsi.koki.room.server.service.RoomService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/rooms")
class RoomEndpoints(
    private val service: RoomService,
    private val mapper: RoomMapper,
    private val publisher: Publisher,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateRoomRequest,
    ): CreateRoomResponse {
        val room = service.create(request, tenantId)
        return CreateRoomResponse(
            roomId = room.id ?: -1
        )
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateRoomRequest,
    ) {
        service.update(id, request, tenantId)
    }

    @PostMapping("/{id}/geolocation")
    fun geo(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: SaveRoomGeoLocationRequest,
    ) {
        service.geo(id, request, tenantId)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        service.delete(id, tenantId)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetRoomResponse {
        val room = service.get(id, tenantId)
        return GetRoomResponse(
            room = mapper.toRoom(room)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "account-id") accountIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "city-id") cityId: Long? = null,
        @RequestParam(required = false, name = "neighborhood-id") neighborhoodId: Long? = null,
        @RequestParam(required = false, name = "total-guests") totalGuests: Int? = null,
        @RequestParam(required = false, name = "min-rooms") minRooms: Int? = null,
        @RequestParam(required = false, name = "max-rooms") maxRooms: Int? = null,
        @RequestParam(required = false, name = "min-bathrooms") minBathrooms: Int? = null,
        @RequestParam(required = false, name = "max-bathrooms") maxBathrooms: Int? = null,
        @RequestParam(required = false, name = "amenity-id") amenityIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "category-id") categoryIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "type") types: List<RoomType> = emptyList(),
        @RequestParam(required = false) status: RoomStatus? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchRoomResponse {
        val rooms = service.search(
            tenantId = tenantId,
            ids = ids,
            accountIds = accountIds,
            cityId = cityId,
            neighborhoodId = neighborhoodId,
            status = status,
            types = types,
            totalGuests = totalGuests,
            minRooms = minRooms,
            maxRooms = maxRooms,
            minBathrooms = minBathrooms,
            maxBathrooms = maxBathrooms,
            amenityIds = amenityIds,
            categoryIds = categoryIds,
            limit = limit,
            offset = offset,
        )
        return SearchRoomResponse(rooms = rooms.map { room -> mapper.toRoomSummary(room) })
    }

    @PostMapping("/{id}/amenities")
    fun addAmenities(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: AddAmenityRequest
    ) {
        service.addAmenities(id, request, tenantId)
    }

    @DeleteMapping("/{id}/amenities/{amenityId}")
    fun removeAmenity(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @PathVariable amenityId: Long,
    ) {
        service.removeAmenity(id, amenityId, tenantId)
    }

    @GetMapping("/{id}/publish")
    fun publish(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        val room = service.startPublishing(id, tenantId)
        if (room != null) {
            publisher.publish(
                PublishRoomCommand(roomId = id, tenantId = tenantId)
            )
        }
    }

    @PostMapping("/{id}/hero-image")
    fun setHeroImage(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: SetHeroImageRequest
    ) {
        service.setHeroImage(id, request, tenantId)
    }
}
