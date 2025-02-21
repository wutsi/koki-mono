package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
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
        assertEquals(0, response.body?.errors)
        assertEquals(1, response.body?.added)
        assertEquals(0, response.body?.updated)

        val taxes = dao.findByCountry("CM")
        assertEquals(1, taxes.size)
        assertEquals("VAT", taxes[0].name)
        assertEquals(237L, taxes[0].juridiction.id)
        assertEquals(19.25, taxes[0].rate)
        assertEquals(true, taxes[0].active)
        assertEquals(0, taxes[0].priority)
    }

    @Test
    fun ca() {
        val response = rest.getForEntity("/v1/sales-taxes/import?country=CA", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(0, response.body?.errors)
        assertEquals(18, response.body?.added)
        assertEquals(1, response.body?.updated)

        val taxes = dao.findByCountry("CA")
        assertEquals(19, taxes.size)

        // Deactivated
        val deactivated = dao.findById(100).get()
        assertFalse(deactivated.active)

        // Canada
        val ca = taxes.filter { tax -> tax.juridiction.id == 1000L }
        assertEquals(1, ca.size)
        assertEquals("GST", ca[0].name)
        assertEquals(5.00, ca[0].rate)
        assertEquals(true, ca[0].active)
        assertEquals(0, ca[0].priority)

        // Alberta
        val ab = taxes.filter { tax -> tax.juridiction.id == 1001L }
        assertEquals(1, ab.size)
        assertEquals("GST", ab[0].name)
        assertEquals(5.00, ab[0].rate)
        assertEquals(true, ab[0].active)
        assertEquals(0, ab[0].priority)

        // British Columbia
        val bc = taxes.filter { tax -> tax.juridiction.id == 1002L }
        assertEquals(2, bc.size)
        assertEquals("GST", bc[0].name)
        assertEquals(5.00, bc[0].rate)
        assertEquals(true, bc[0].active)
        assertEquals(0, bc[0].priority)

        assertEquals("PST", bc[1].name)
        assertEquals(7.00, bc[1].rate)
        assertEquals(true, bc[1].active)
        assertEquals(0, bc[1].priority)

        // Manitoba
        val mb = taxes.filter { tax -> tax.juridiction.id == 1003L }
        assertEquals(2, mb.size)
        assertEquals("GST", mb[0].name)
        assertEquals(5.00, mb[0].rate)
        assertEquals(true, mb[0].active)
        assertEquals(0, mb[0].priority)

        assertEquals("PST", mb[1].name)
        assertEquals(7.00, mb[1].rate)
        assertEquals(true, mb[1].active)
        assertEquals(0, mb[1].priority)

        // New Brunswick
        val nb = taxes.filter { tax -> tax.juridiction.id == 1004L }
        assertEquals(1, nb.size)
        assertEquals("HST", nb[0].name)
        assertEquals(15.00, nb[0].rate)
        assertEquals(true, nb[0].active)

        //  Newfoundland and Labrador
        val nl = taxes.filter { tax -> tax.juridiction.id == 1005L }
        assertEquals(1, nl.size)
        assertEquals("HST", nl[0].name)
        assertEquals(15.00, nl[0].rate)
        assertEquals(true, nl[0].active)

        // Northwest Territories
        val nt = taxes.filter { tax -> tax.juridiction.id == 1006L }
        assertEquals(1, nt.size)
        assertEquals("GST", nt[0].name)
        assertEquals(5.00, nt[0].rate)
        assertEquals(true, nt[0].active)
        assertEquals(0, nt[0].priority)

        //  Nova Scotia
        val ns = taxes.filter { tax -> tax.juridiction.id == 1007L }
        assertEquals(1, ns.size)
        assertEquals("HST", ns[0].name)
        assertEquals(15.00, ns[0].rate)
        assertEquals(true, ns[0].active)

        //  Nunavut
        val nv = taxes.filter { tax -> tax.juridiction.id == 1008L }
        assertEquals(1, nv.size)
        assertEquals("GST", nv[0].name)
        assertEquals(5.00, nv[0].rate)
        assertEquals(true, nv[0].active)

        //  Ontario
        val on = taxes.filter { tax -> tax.juridiction.id == 1009L }
        assertEquals(1, on.size)
        assertEquals("HST", on[0].name)
        assertEquals(13.00, on[0].rate)
        assertEquals(true, on[0].active)

        //  Prince Edward Island
        val pe = taxes.filter { tax -> tax.juridiction.id == 1010L }
        assertEquals(1, pe.size)
        assertEquals("HST", pe[0].name)
        assertEquals(15.00, pe[0].rate)
        assertEquals(true, pe[0].active)

        //  Quebec
        val qc = taxes.filter { tax -> tax.juridiction.id == 1011L && tax.active }
        assertEquals(2, qc.size)
        assertEquals("GST", qc[0].name)
        assertEquals(5.00, qc[0].rate)
        assertEquals(true, qc[0].active)
        assertEquals(0, qc[0].priority)

        assertEquals("PST", qc[1].name)
        assertEquals(9.975, qc[1].rate)
        assertEquals(true, qc[1].active)
        assertEquals(1, qc[1].priority)

        //  Saskatchewan
        val sk = taxes.filter { tax -> tax.juridiction.id == 1012L && tax.active }
        assertEquals(2, sk.size)
        assertEquals("GST", sk[0].name)
        assertEquals(5.00, sk[0].rate)
        assertEquals(true, sk[0].active)
        assertEquals(0, sk[0].priority)

        assertEquals("PST", sk[1].name)
        assertEquals(6.0, sk[1].rate)
        assertEquals(true, sk[1].active)
        assertEquals(0, sk[1].priority)

        // Yukon
        val yk = taxes.filter { tax -> tax.juridiction.id == 1013L }
        assertEquals(1, yk.size)
        assertEquals("GST", yk[0].name)
        assertEquals(5.00, yk[0].rate)
        assertEquals(true, yk[0].active)
        assertEquals(0, yk[0].priority)
    }

    @Test
    fun `bad country`() {
        val response = rest.getForEntity("/v1/sales-taxes/import?country=zzz", ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.SALES_TAX_COUNTRY_NOT_SUPPORTED, response.body?.error?.code)
    }

    @Test
    fun `bad jurdiction`() {
        val response = rest.getForEntity("/v1/sales-taxes/import?country=YY", ImportResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body!!.errors)
        assertEquals(ErrorCode.JURIDICTION_NOT_FOUND, response.body!!.errorMessages[0].code)
    }
}
