package com.wutsi.koki.room.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.server.command.PublishRoomCommand
import com.wutsi.koki.room.server.dao.RoomRepository
import com.wutsi.koki.room.server.service.RoomPublisherValidator
import jakarta.validation.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/room/PublishRoomEndpoint.sql"])
class PublishRoomEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var publisher: Publisher

    @MockitoBean
    private lateinit var validator: RoomPublisherValidator

    @Autowired
    private lateinit var dao: RoomRepository

    @Test
    fun unknown() {
        val response = rest.getForEntity("/v1/rooms/100/publish", ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.ROOM_INVALID_STATUS, response.body?.error?.code)

        verify(publisher, never()).publish(any())
    }

    @Test
    fun draft() {
        val response = rest.getForEntity("/v1/rooms/101/publish", Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val room = dao.findById(101L).get()
        assertEquals(RoomStatus.PUBLISHING, room.status)
        assertEquals(USER_ID, room.publishedById)

        val cmd = argumentCaptor<PublishRoomCommand>()
        verify(publisher).publish(cmd.capture())
        assertEquals(101L, cmd.firstValue.roomId)
        assertEquals(TENANT_ID, cmd.firstValue.tenantId)
    }

    @Test
    fun publishing() {
        val response = rest.getForEntity("/v1/rooms/102/publish", Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val room = dao.findById(102L).get()
        assertEquals(RoomStatus.PUBLISHING, room.status)
        assertEquals(null, room.publishedById)

        verify(publisher, never()).publish(any())
    }

    @Test
    fun published() {
        val response = rest.getForEntity("/v1/rooms/103/publish", Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val room = dao.findById(103L).get()
        assertEquals(RoomStatus.PUBLISHED, room.status)
        assertEquals(null, room.publishedById)

        verify(publisher, never()).publish(any())
    }

    @Test
    fun `validation error`() {
        val ex = ValidationException(ErrorCode.ROOM_GEOLOCATION_MISSING)
        doThrow(ex).whenever(validator).validate(any())

        val response = rest.getForEntity("/v1/rooms/103/publish", ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.ROOM_GEOLOCATION_MISSING, response.body?.error?.code)

        verify(publisher, never()).publish(any())
    }
}
