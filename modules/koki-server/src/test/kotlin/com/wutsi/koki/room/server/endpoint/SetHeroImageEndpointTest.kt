package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.room.dto.SetHeroImageRequest
import com.wutsi.koki.room.server.dao.RoomRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/SetHeroImageEndpoint.sql"])
class SetHeroImageEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoomRepository

    @Test
    fun update() {
        val request = SetHeroImageRequest(
            fileId = 110
        )
        val response = rest.postForEntity("/v1/rooms/111/hero-image", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val room = dao.findById(111).get()

        assertEquals(request.fileId, room.heroImageId)
    }

    @Test
    fun `bad owner-type`() {
        val request = SetHeroImageRequest(
            fileId = 111
        )
        val response = rest.postForEntity("/v1/rooms/111/hero-image", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.ROOM_IMAGE_NOT_OWNED, response?.body?.error?.code)
    }

    @Test
    fun `bad owner-id`() {
        val request = SetHeroImageRequest(
            fileId = 112
        )
        val response = rest.postForEntity("/v1/rooms/111/hero-image", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.ROOM_IMAGE_NOT_OWNED, response?.body?.error?.code)
    }

    @Test
    fun `bad type`() {
        val request = SetHeroImageRequest(
            fileId = 113
        )
        val response = rest.postForEntity("/v1/rooms/111/hero-image", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.ROOM_IMAGE_NOT_VALID, response?.body?.error?.code)
    }
}
