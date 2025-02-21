package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.refdata.server.dao.JuridictionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/refdata/ImportJuridictionEndpoint.sql"])
class ImportJuridictionEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: JuridictionRepository

    @Test
    fun cm() {
        val response = rest.getForEntity("/v1/juridictions/import?country=CM", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(0, response.body?.errors)
        assertEquals(1, response.body?.added)
        assertEquals(0, response.body?.updated)

        val taxes = dao.findByCountry("CM")
        assertEquals(1, taxes.size)
        assertEquals("CM", taxes[0].country)
        assertEquals(null, taxes[0].stateId)
    }

    @Test
    fun ca() {
        val response = rest.getForEntity("/v1/juridictions/import?country=CA", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(0, response.body?.errors)
        assertEquals(13, response.body?.added)
        assertEquals(1, response.body?.updated)

        val juridictions = dao.findByCountry("CA")
        assertEquals(14, juridictions.size)

        // CA
        assertEquals(1000L, juridictions[0].id)
        assertEquals("CA", juridictions[0].country)
        assertEquals(null, juridictions[0].stateId)

        // Alberta
        assertEquals(1001L, juridictions[1].id)
        assertEquals("CA", juridictions[1].country)
        assertEquals(101, juridictions[1].stateId)

        // British Columbia
        assertEquals(1002L, juridictions[2].id)
        assertEquals("CA", juridictions[2].country)
        assertEquals(102, juridictions[2].stateId)

        // Manitoba
        assertEquals(1003L, juridictions[3].id)
        assertEquals("CA", juridictions[3].country)
        assertEquals(103, juridictions[3].stateId)

        // New Brunswick
        assertEquals(1004L, juridictions[4].id)
        assertEquals("CA", juridictions[4].country)
        assertEquals(104, juridictions[4].stateId)

        //  Newfoundland and Labrador
        assertEquals(1005L, juridictions[5].id)
        assertEquals("CA", juridictions[5].country)
        assertEquals(105, juridictions[5].stateId)

        // Northwest Territories
        assertEquals(1006L, juridictions[6].id)
        assertEquals("CA", juridictions[6].country)
        assertEquals(106, juridictions[6].stateId)

        //  Nova Scotia
        assertEquals(1007L, juridictions[7].id)
        assertEquals("CA", juridictions[7].country)
        assertEquals(107, juridictions[7].stateId)

        //  Nunavut
        assertEquals(1008L, juridictions[8].id)
        assertEquals("CA", juridictions[8].country)
        assertEquals(108, juridictions[8].stateId)

        //  Ontario
        assertEquals(1009L, juridictions[9].id)
        assertEquals("CA", juridictions[9].country)
        assertEquals(109, juridictions[9].stateId)

        //  Prince Edward Island
        assertEquals(1010L, juridictions[10].id)
        assertEquals("CA", juridictions[10].country)
        assertEquals(110, juridictions[10].stateId)

        //  Quebec
        assertEquals(1011L, juridictions[11].id)
        assertEquals("CA", juridictions[11].country)
        assertEquals(111, juridictions[11].stateId)

        //  Saskatchewan
        assertEquals(1012L, juridictions[12].id)
        assertEquals("CA", juridictions[12].country)
        assertEquals(112, juridictions[12].stateId)

        // Yukon
        assertEquals(1013L, juridictions[13].id)
        assertEquals("CA", juridictions[13].country)
        assertEquals(113, juridictions[13].stateId)
    }

    @Test
    fun `bad country`() {
        val response = rest.getForEntity("/v1/juridictions/import?country=zzz", ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.JURIDICTION_COUNTRY_NOT_SUPPORTED, response.body?.error?.code)
    }

    @Test
    fun `bad state`() {
        val response = rest.getForEntity("/v1/juridictions/import?country=YY", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body!!.errors)
        assertEquals(ErrorCode.LOCATION_NOT_FOUND, response.body!!.errorMessages[0].code)
    }
}
