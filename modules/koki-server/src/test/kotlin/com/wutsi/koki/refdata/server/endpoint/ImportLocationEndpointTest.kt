package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.dao.LocationRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportLocationEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: LocationRepository

    @Sql(value = ["/db/test/clean.sql"])
    @Test
    fun cm() {
        val response = rest.getForEntity("/v1/locations/import?country=CM", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val countries = dao.findByType(LocationType.COUNTRY)
        assertEquals(1, countries.size)
        assertEquals("Cameroon", countries[0].name)
        assertEquals("CM", countries[0].country)

        val states = dao.findByType(LocationType.STATE)
        assertEquals(10, states.size)
        states.forEach { state -> assertEquals(countries[0].id, state.parentId) }

        val stateIds = states.map { it.id }
        val cities = dao.findByType(LocationType.CITY)
        cities.forEach { city -> assertTrue(stateIds.contains(city.parentId)) }
    }

    @Sql(value = ["/db/test/clean.sql"])
    @Test
    fun ca() {
        val response = rest.getForEntity("/v1/locations/import?country=CA", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val countries = dao.findByType(LocationType.COUNTRY)
        assertEquals(1, countries.size)
        assertEquals("Canada", countries[0].name)
        assertEquals("CA", countries[0].country)

        val states = dao.findByType(LocationType.STATE)
        assertEquals(13, states.size)
        states.forEach { state -> assertEquals(countries[0].id, state.parentId) }

        val stateIds = states.map { it.id }
        val cities = dao.findByType(LocationType.CITY)
        cities.forEach { city -> assertTrue(stateIds.contains(city.parentId)) }
    }

    @Sql(value = ["/db/test/clean.sql"])
    @Test
    fun us() {
        val response = rest.getForEntity("/v1/locations/import?country=US", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val countries = dao.findByType(LocationType.COUNTRY)
        assertEquals(1, countries.size)
        assertEquals("United States", countries[0].name)
        assertEquals("US", countries[0].country)

        val states = dao.findByType(LocationType.STATE)
        assertEquals(51, states.size)
        states.forEach { state -> assertEquals(countries[0].id, state.parentId) }

        val stateIds = states.map { it.id }
        val cities = dao.findByType(LocationType.CITY)
        cities.forEach { city -> assertTrue(stateIds.contains(city.parentId)) }
    }

    @Sql(value = ["/db/test/clean.sql"])
    @Test
    fun fr() {
        val response = rest.getForEntity("/v1/locations/import?country=FR", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val countries = dao.findByType(LocationType.COUNTRY)
        assertEquals(1, countries.size)
        assertEquals("FR", countries[0].country)

        val states = dao.findByType(LocationType.STATE)
        assertEquals(13, states.size)
        states.forEach { state -> assertEquals(countries[0].id, state.parentId) }

        val stateIds = states.map { it.id }
        val cities = dao.findByType(LocationType.CITY)
        cities.forEach { city -> assertTrue(stateIds.contains(city.parentId)) }
    }
}
