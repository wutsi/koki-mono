package com.wutsi.koki.sdk

import com.wutsi.koki.room.dto.CreateRoomRequest
import com.wutsi.koki.room.dto.CreateRoomResponse
import com.wutsi.koki.room.dto.GetRoomResponse
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.SearchRoomResponse
import com.wutsi.koki.room.dto.UpdateRoomRequest
import org.springframework.web.client.RestTemplate

class KokiRooms(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val ROOM_PATH_PREFIX = "/v1/rooms"
    }

    fun create(request: CreateRoomRequest): CreateRoomResponse {
        val url = urlBuilder.build(ROOM_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateRoomResponse::class.java).body
    }

    fun update(id: Long, request: UpdateRoomRequest) {
        val url = urlBuilder.build("$ROOM_PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$ROOM_PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun room(id: Long): GetRoomResponse {
        val url = urlBuilder.build("$ROOM_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetRoomResponse::class.java).body
    }

    fun rooms(
        ids: List<Long>,
        cityId: Long?,
        status: RoomStatus?,
        type: RoomType?,
        totalGuests: Int?,
        limit: Int,
        offset: Int,
    ): SearchRoomResponse {
        val url = urlBuilder.build(
            ROOM_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "city-id" to cityId,
                "status" to status,
                "type" to type,
                "total-guests" to totalGuests,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchRoomResponse::class.java).body
    }
}
