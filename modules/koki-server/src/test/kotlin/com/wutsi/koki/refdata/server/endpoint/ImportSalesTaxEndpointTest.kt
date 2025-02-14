package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.server.dao.SalesTaxRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/refdata/ImportSalesTaxEndpoint.sql"])
class ImportSalesTaxEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: SalesTaxRepository

    @Test
    fun cm() {
        val response = rest.getForEntity("/v1/sales-taxes/import?country=CM", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val taxes = dao.findByCountry("CM")
        assertEquals(1, taxes.size)
        assertEquals("VAT", taxes[0].name)
        assertEquals("CM", taxes[0].country)
        assertEquals(19.25, taxes[0].rate)
        assertEquals(null, taxes[0].stateId)
        assertEquals(true, taxes[0].active)
        assertEquals(0, taxes[0].priority)
    }

    @Test
    fun ca() {
        val response = rest.getForEntity("/v1/sales-taxes/import?country=CA", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val taxes = dao.findByCountry("CA")
        assertEquals(18, taxes.size)

        // Deactivated
        val deactivated = dao.findById(100).get()
        assertFalse(deactivated.active)

        // Alberta
        val ab = taxes.filter { tax -> tax.stateId == 101L }
        assertEquals(1, ab.size)
        assertEquals("GST", ab[0].name)
        assertEquals("CA", ab[0].country)
        assertEquals(5.00, ab[0].rate)
        assertEquals(101L, ab[0].stateId)
        assertEquals(true, ab[0].active)
        assertEquals(0, ab[0].priority)

        // British Columbia
        val bc = taxes.filter { tax -> tax.stateId == 102L }
        assertEquals(2, bc.size)
        assertEquals("GST", bc[0].name)
        assertEquals("CA", bc[0].country)
        assertEquals(5.00, bc[0].rate)
        assertEquals(102L, bc[0].stateId)
        assertEquals(true, bc[0].active)
        assertEquals(0, bc[0].priority)

        assertEquals("PST", bc[1].name)
        assertEquals("CA", bc[1].country)
        assertEquals(7.00, bc[1].rate)
        assertEquals(102L, bc[1].stateId)
        assertEquals(true, bc[1].active)
        assertEquals(0, bc[1].priority)

        // Manitoba
        val mb = taxes.filter { tax -> tax.stateId == 103L }
        assertEquals(2, mb.size)
        assertEquals("GST", mb[0].name)
        assertEquals("CA", mb[0].country)
        assertEquals(5.00, mb[0].rate)
        assertEquals(103L, mb[0].stateId)
        assertEquals(true, mb[0].active)
        assertEquals(0, mb[0].priority)

        assertEquals("PST", mb[1].name)
        assertEquals("CA", mb[1].country)
        assertEquals(7.00, mb[1].rate)
        assertEquals(103L, mb[1].stateId)
        assertEquals(true, mb[1].active)
        assertEquals(0, mb[1].priority)

        // New Brunswick
        val nb = taxes.filter { tax -> tax.stateId == 104L }
        assertEquals(1, nb.size)
        assertEquals("HST", nb[0].name)
        assertEquals("CA", nb[0].country)
        assertEquals(15.00, nb[0].rate)
        assertEquals(104L, nb[0].stateId)
        assertEquals(true, nb[0].active)

        //  Newfoundland and Labrador
        val nl = taxes.filter { tax -> tax.stateId == 105L }
        assertEquals(1, nl.size)
        assertEquals("HST", nl[0].name)
        assertEquals("CA", nl[0].country)
        assertEquals(15.00, nl[0].rate)
        assertEquals(105L, nl[0].stateId)
        assertEquals(true, nl[0].active)

        // Northwest Territories
        val nt = taxes.filter { tax -> tax.stateId == 106L }
        assertEquals(1, nt.size)
        assertEquals("GST", nt[0].name)
        assertEquals("CA", nt[0].country)
        assertEquals(5.00, nt[0].rate)
        assertEquals(106L, nt[0].stateId)
        assertEquals(true, nt[0].active)
        assertEquals(0, nt[0].priority)

        //  Nova Scotia
        val ns = taxes.filter { tax -> tax.stateId == 107L }
        assertEquals(1, ns.size)
        assertEquals("HST", ns[0].name)
        assertEquals("CA", ns[0].country)
        assertEquals(15.00, ns[0].rate)
        assertEquals(107L, ns[0].stateId)
        assertEquals(true, ns[0].active)

        //  Nunavut
        val nv = taxes.filter { tax -> tax.stateId == 108L }
        assertEquals(1, nv.size)
        assertEquals("GST", nv[0].name)
        assertEquals("CA", nv[0].country)
        assertEquals(5.00, nv[0].rate)
        assertEquals(108L, nv[0].stateId)
        assertEquals(true, nv[0].active)

        //  Ontario
        val on = taxes.filter { tax -> tax.stateId == 109L }
        assertEquals(1, on.size)
        assertEquals("HST", on[0].name)
        assertEquals("CA", on[0].country)
        assertEquals(13.00, on[0].rate)
        assertEquals(109L, on[0].stateId)
        assertEquals(true, on[0].active)

        //  Prince Edward Island
        val pe = taxes.filter { tax -> tax.stateId == 110L }
        assertEquals(1, pe.size)
        assertEquals("HST", pe[0].name)
        assertEquals("CA", pe[0].country)
        assertEquals(15.00, pe[0].rate)
        assertEquals(110L, pe[0].stateId)
        assertEquals(true, pe[0].active)

        //  Quebec
        val qc = taxes.filter { tax -> tax.stateId == 111L && tax.active }
        assertEquals(2, qc.size)
        assertEquals("GST", qc[0].name)
        assertEquals("CA", qc[0].country)
        assertEquals(5.00, qc[0].rate)
        assertEquals(111L, qc[0].stateId)
        assertEquals(true, qc[0].active)
        assertEquals(0, qc[0].priority)

        assertEquals("PST", qc[1].name)
        assertEquals("CA", qc[1].country)
        assertEquals(9.975, qc[1].rate)
        assertEquals(111L, qc[1].stateId)
        assertEquals(true, qc[1].active)
        assertEquals(1, qc[1].priority)

        //  Saskatchewan
        val sk = taxes.filter { tax -> tax.stateId == 112L && tax.active }
        assertEquals(2, sk.size)
        assertEquals("GST", sk[0].name)
        assertEquals("CA", sk[0].country)
        assertEquals(5.00, sk[0].rate)
        assertEquals(112L, sk[0].stateId)
        assertEquals(true, sk[0].active)
        assertEquals(0, sk[0].priority)

        assertEquals("PST", sk[1].name)
        assertEquals("CA", sk[1].country)
        assertEquals(6.0, sk[1].rate)
        assertEquals(112L, sk[1].stateId)
        assertEquals(true, sk[1].active)
        assertEquals(0, sk[1].priority)

        // Yukon
        val yk = taxes.filter { tax -> tax.stateId == 113L }
        assertEquals(1, yk.size)
        assertEquals("GST", yk[0].name)
        assertEquals("CA", yk[0].country)
        assertEquals(5.00, yk[0].rate)
        assertEquals(113L, yk[0].stateId)
        assertEquals(true, yk[0].active)
        assertEquals(0, yk[0].priority)
    }
}
