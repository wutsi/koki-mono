package com.wutsi.koki.sdk

import com.wutsi.koki.room.dto.CreateRoomUnitRequest
import com.wutsi.koki.room.dto.CreateRoomUnitResponse
import com.wutsi.koki.room.dto.GetRoomUnitResponse
import com.wutsi.koki.room.dto.RoomUnitStatus
import com.wutsi.koki.room.dto.SearchRoomUnitResponse
import com.wutsi.koki.room.dto.UpdateRoomUnitRequest
import org.springframework.web.client.RestTemplate

class KokiRoomUnits(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/room-units"
    }

    fun create(request: CreateRoomUnitRequest): CreateRoomUnitResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateRoomUnitResponse::class.java).body
    }

    fun update(id: Long, request: UpdateRoomUnitRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun roomUnit(id: Long): GetRoomUnitResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetRoomUnitResponse::class.java).body
    }

    fun roomUnits(
        ids: List<Long>,
        roomId: Long?,
        status: RoomUnitStatus?,
        limit: Int,
        offset: Int,
    ): SearchRoomUnitResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "room-id" to roomId,
                "status" to status,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchRoomUnitResponse::class.java).body
    }
}
