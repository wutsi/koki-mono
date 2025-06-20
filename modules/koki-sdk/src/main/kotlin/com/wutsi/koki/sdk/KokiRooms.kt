package com.wutsi.koki.sdk

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
        neighborhoodId: Long?,
        status: RoomStatus?,
        totalGuests: Int?,
        types: List<RoomType>,
        amenityIds: List<Long>,
        minRooms: Int?,
        maxRooms: Int?,
        minBathrooms: Int?,
        maxBathrooms: Int?,
        categoryIds: List<Long>,
        accountIds: List<Long>,
        accountManagerIds: List<Long>,
        limit: Int,
        offset: Int,
    ): SearchRoomResponse {
        val url = urlBuilder.build(
            ROOM_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "city-id" to cityId,
                "status" to status,
                "type" to types,
                "total-guests" to totalGuests,
                "amenity-id" to amenityIds,
                "min-rooms" to minRooms,
                "max-rooms" to maxRooms,
                "min-bathrooms" to minBathrooms,
                "max-bathrooms" to maxBathrooms,
                "neighborhood-id" to neighborhoodId,
                "category-id" to categoryIds,
                "account-id" to accountIds,
                "account-manager-id" to accountManagerIds,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchRoomResponse::class.java).body
    }

    fun saveGeolocation(roomId: Long, request: SaveRoomGeoLocationRequest) {
        val url = urlBuilder.build("$ROOM_PATH_PREFIX/$roomId/geolocation")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun addAmenities(roomId: Long, request: AddAmenityRequest) {
        val url = urlBuilder.build("$ROOM_PATH_PREFIX/$roomId/amenities")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun removeAmenity(roomId: Long, amenityId: Long) {
        val url = urlBuilder.build("$ROOM_PATH_PREFIX/$roomId/amenities/$amenityId")
        rest.delete(url)
    }

    fun publish(roomId: Long) {
        val url = urlBuilder.build("$ROOM_PATH_PREFIX/$roomId/publish")
        rest.getForEntity(url, Any::class.java)
    }

    fun setHeroImage(roomId: Long, request: SetHeroImageRequest) {
        val url = urlBuilder.build("$ROOM_PATH_PREFIX/$roomId/hero-image")
        rest.postForEntity(url, request, Any::class.java)
    }
}
