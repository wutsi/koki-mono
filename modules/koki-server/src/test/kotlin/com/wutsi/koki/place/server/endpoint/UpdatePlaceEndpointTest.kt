package com.wutsi.koki.place.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.place.dto.event.PlaceUpdatedEvent
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/place/UpdatePlaceEndpoint.sql"])
class UpdatePlaceEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun neighborhood() {
        // WHEN
        val response = rest.postForEntity("/v1/places/100", emptyMap<String, String>(), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val event = argumentCaptor<PlaceUpdatedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(100L, event.firstValue.placeId)
    }
}
