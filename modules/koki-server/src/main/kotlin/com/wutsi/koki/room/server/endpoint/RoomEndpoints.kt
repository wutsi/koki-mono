package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.room.dto.AddAmenityRequest
import com.wutsi.koki.room.dto.AddImageRequest
import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.CreateRoomResponse
import com.wutsi.koki.room.dto.GetRoomResponse
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.SearchRoomResponse
import com.wutsi.koki.room.dto.UpdateRoomRequest
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
        @RequestParam(required = false, name = "city-id") cityId: Long? = null,
        @RequestParam(required = false, name = "total-guests") totalGuests: Int? = null,
        @RequestParam(required = false) status: RoomStatus? = null,
        @RequestParam(required = false) type: RoomType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchRoomResponse {
        val rooms = service.search(
            tenantId = tenantId,
            ids = ids,
            cityId = cityId,
            status = status,
            type = type,
            totalGuests = totalGuests,
            limit = limit,
            offset = offset,
        )
        return SearchRoomResponse(
            rooms = rooms.map { room -> mapper.toRoomSummary(room) }
        )
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

    @PostMapping("/{id}/images")
    fun addImages(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: AddImageRequest
    ) {
        service.addImage(id, request, tenantId)
    }

    @DeleteMapping("/{id}/images/{fileId}")
    fun removeImage(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @PathVariable fileId: Long,
    ) {
        service.removeImage(id, fileId, tenantId)
    }
}
