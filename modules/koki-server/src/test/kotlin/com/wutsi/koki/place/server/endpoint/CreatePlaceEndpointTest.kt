package com.wutsi.koki.place.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.CreatePlaceResponse
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.event.PlaceCreatedEvent
import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/place/CreatePlaceEndpoint.sql"])
class CreatePlaceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: PlaceRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun neighborhood() {
        // WHEN
        val request = CreatePlaceRequest(
            name = "Côte-des-Neiges",
            type = PlaceType.NEIGHBORHOOD,
            neighbourhoodId = 222L,
        )
        val response = rest.postForEntity("/v1/places", request, CreatePlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val placeId = response.body!!.placeId
        val place = dao.findById(placeId).get()

        assertEquals(request.type, place.type)
        assertEquals(request.neighbourhoodId, place.neighbourhoodId)
        assertEquals(111L, place.cityId)
        assertEquals(PlaceStatus.DRAFT, place.status)
        assertEquals("Côte-des-Neiges", place.name)
        assertEquals(null, place.introduction)
        assertEquals(null, place.summary)
        assertEquals(null, place.description)
        assertEquals("cote-des-neiges", place.asciiName)
        assertEquals(null, place.introductionFr)
        assertEquals(null, place.summaryFr)
        assertEquals(null, place.descriptionFr)
        assertEquals(null, place.rating)
        assertEquals(null, place.latitude)
        assertEquals(null, place.longitude)
        assertEquals(USER_ID, place.createdById)
        assertEquals(USER_ID, place.modifiedById)
        assertFalse(place.deleted)
        assertNull(place.deletedAt)

        val event = argumentCaptor<PlaceCreatedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(placeId, event.firstValue.placeId)
    }

    @Test
    fun duplicate() {
        // WHEN
        val request = CreatePlaceRequest(
            name = "Westmount",
            type = PlaceType.NEIGHBORHOOD,
            neighbourhoodId = 333L,
        )
        val response = rest.postForEntity("/v1/places", request, ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.PLACE_DUPLICATE_NAME, response.body?.error?.code)
    }
}
