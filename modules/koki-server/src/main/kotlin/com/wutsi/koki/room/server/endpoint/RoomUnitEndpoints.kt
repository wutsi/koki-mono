package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.room.dto.CreateRoomUnitRequest
import com.wutsi.koki.room.dto.CreateRoomUnitResponse
import com.wutsi.koki.room.dto.GetRoomUnitResponse
import com.wutsi.koki.room.dto.RoomUnitStatus
import com.wutsi.koki.room.dto.SearchRoomUnitResponse
import com.wutsi.koki.room.dto.UpdateRoomUnitRequest
import com.wutsi.koki.room.server.mapper.RoomUnitMapper
import com.wutsi.koki.room.server.service.RoomUnitService
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
@RequestMapping("/v1/room-units")
class RoomUnitEndpoints(
    private val service: RoomUnitService,
    private val mapper: RoomUnitMapper,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateRoomUnitRequest,
    ): CreateRoomUnitResponse {
        val roomUnit = service.create(request, tenantId)
        return CreateRoomUnitResponse(
            roomUnitId = roomUnit.id ?: -1
        )
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateRoomUnitRequest,
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
    ): GetRoomUnitResponse {
        val roomUnit = service.get(id, tenantId)
        return GetRoomUnitResponse(
            roomUnit = mapper.toRoomUnit(roomUnit)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "room-id") roomId: Long? = null,
        @RequestParam(required = false) status: RoomUnitStatus? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchRoomUnitResponse {
        val roomUnits = service.search(
            tenantId = tenantId,
            ids = ids,
            roomId = roomId,
            status = status,
            limit = limit,
            offset = offset,
        )
        return SearchRoomUnitResponse(
            roomUnits = roomUnits.map { unit -> mapper.toRoomUnitSummary(unit) }
        )
    }
}
