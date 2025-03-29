package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.form.dto.SearchFormResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/SearchFormEndpoint.sql"])
class SearchFormEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/forms", SearchFormResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val forms = result.body!!.forms

        assertEquals(4, forms.size)
    }

    @Test
    fun `filter by active`() {
        val result = rest.getForEntity("/v1/forms?active=false", SearchFormResponse::class.java)

        val forms = result.body!!.forms
        assertEquals(2, forms.size)
        assertEquals(100L, forms[0].id)
        assertEquals(110L, forms[1].id)
    }

    @Test
    fun `filter by ids`() {
        val result = rest.getForEntity("/v1/forms?id=100&id=120&id=130", SearchFormResponse::class.java)

        val forms = result.body!!.forms
        assertEquals(3, forms.size)
        assertEquals(100L, forms[0].id)
        assertEquals(120L, forms[1].id)
        assertEquals(130L, forms[2].id)
    }

    @Test
    fun `filter by owner`() {
        val result = rest.getForEntity("/v1/forms?owner-id=111&owner-type=CONTACT", SearchFormResponse::class.java)

        val forms = result.body!!.forms
        assertEquals(2, forms.size)
        assertEquals(100L, forms[0].id)
        assertEquals(130L, forms[1].id)
    }

    @Test
    fun `form of another tenant`() {
        val result = rest.getForEntity("/v1/forms?id=200", SearchFormResponse::class.java)

        val forms = result.body!!.forms
        assertEquals(0, forms.size)
    }
}
