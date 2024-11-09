package com.wutsi.koki.tenant.server.server.endpoint

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
        assertEquals("100", forms[0].id)
        assertEquals("Form 100", forms[0].title)

        assertEquals("110", forms[1].id)
        assertEquals("Form 110", forms[1].title)

        assertEquals("120", forms[2].id)
        assertEquals("Form 120", forms[2].title)

        assertEquals("130", forms[3].id)
        assertEquals("Form 130", forms[3].title)
    }

    @Test
    fun `filter by active`() {
        val result =
            rest.getForEntity("/v1/forms?active=false&sort-by=TITLE&asc=true", SearchFormResponse::class.java)

        val forms = result.body!!.forms
        assertEquals(1, forms.size)
        assertEquals("120", forms[0].id)
    }

    @Test
    fun `filter by ids`() {
        val result =
            rest.getForEntity(
                "/v1/forms?id=100&id=120&id=130&sort-by=CREATED_AT&asc=false",
                SearchFormResponse::class.java
            )

        val forms = result.body!!.forms
        assertEquals(3, forms.size)
        assertEquals("130", forms[0].id)
        assertEquals("120", forms[1].id)
        assertEquals("100", forms[2].id)
    }

    @Test
    fun `form of another tenant`() {
        val result =
            rest.getForEntity("/v1/forms?id=200", SearchFormResponse::class.java)

        val forms = result.body!!.forms
        assertEquals(0, forms.size)
    }
}
