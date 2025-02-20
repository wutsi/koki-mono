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

        val taxes = dao.findByCountry("CM")
        assertEquals(1, taxes.size)
        assertEquals("CM", taxes[0].country)
        assertEquals(null, taxes[0].stateId)
    }

    //    @Test
    fun ca() {
        val response = rest.getForEntity("/v1/juridictions/import?country=CA", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val juridictions = dao.findByCountry("CA")
        assertEquals(18, juridictions.size)

        // Alberta
        val ab = juridictions.filter { tax -> tax.stateId == 101L }
        assertEquals(1, ab.size)
        assertEquals("CA", ab[0].country)
        assertEquals(101L, ab[0].stateId)

        // British Columbia
        val bc = juridictions.filter { tax -> tax.stateId == 102L }
        assertEquals(2, bc.size)
        assertEquals("CA", bc[0].country)
        assertEquals(102L, bc[0].stateId)

        assertEquals("CA", bc[1].country)
        assertEquals(102L, bc[1].stateId)

        // Manitoba
        val mb = juridictions.filter { tax -> tax.stateId == 103L }
        assertEquals(2, mb.size)
        assertEquals("CA", mb[0].country)
        assertEquals(103L, mb[0].stateId)

        assertEquals("CA", mb[1].country)
        assertEquals(103L, mb[1].stateId)

        // New Brunswick
        val nb = juridictions.filter { tax -> tax.stateId == 104L }
        assertEquals(1, nb.size)
        assertEquals("CA", nb[0].country)
        assertEquals(104L, nb[0].stateId)

        //  Newfoundland and Labrador
        val nl = juridictions.filter { tax -> tax.stateId == 105L }
        assertEquals(1, nl.size)
        assertEquals("CA", nl[0].country)
        assertEquals(105L, nl[0].stateId)

        // Northwest Territories
        val nt = juridictions.filter { tax -> tax.stateId == 106L }
        assertEquals(1, nt.size)
        assertEquals("CA", nt[0].country)
        assertEquals(106L, nt[0].stateId)

        //  Nova Scotia
        val ns = juridictions.filter { tax -> tax.stateId == 107L }
        assertEquals(1, ns.size)
        assertEquals("CA", ns[0].country)
        assertEquals(107L, ns[0].stateId)

        //  Nunavut
        val nv = juridictions.filter { tax -> tax.stateId == 108L }
        assertEquals(1, nv.size)
        assertEquals("CA", nv[0].country)
        assertEquals(108L, nv[0].stateId)

        //  Ontario
        val on = juridictions.filter { tax -> tax.stateId == 109L }
        assertEquals(1, on.size)
        assertEquals("CA", on[0].country)
        assertEquals(109L, on[0].stateId)

        //  Prince Edward Island
        val pe = juridictions.filter { tax -> tax.stateId == 110L }
        assertEquals(1, pe.size)
        assertEquals("CA", pe[0].country)
        assertEquals(110L, pe[0].stateId)

        //  Quebec
        val qc = juridictions.filter { tax -> tax.stateId == 111L }
        assertEquals(2, qc.size)
        assertEquals("CA", qc[0].country)
        assertEquals(111L, qc[0].stateId)

        assertEquals("CA", qc[1].country)
        assertEquals(111L, qc[1].stateId)

        //  Saskatchewan
        val sk = juridictions.filter { tax -> tax.stateId == 112L }
        assertEquals(2, sk.size)
        assertEquals("CA", sk[0].country)
        assertEquals(112L, sk[0].stateId)

        assertEquals("CA", sk[1].country)
        assertEquals(112L, sk[1].stateId)

        // Yukon
        val yk = juridictions.filter { tax -> tax.stateId == 113L }
        assertEquals(1, yk.size)
        assertEquals("CA", yk[0].country)
        assertEquals(113L, yk[0].stateId)
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
