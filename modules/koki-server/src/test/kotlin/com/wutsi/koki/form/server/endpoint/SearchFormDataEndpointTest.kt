package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.form.dto.SearchFormDataResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/SearchFormDataEndpoint.sql"])
class SearchFormDataEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/form-data", SearchFormDataResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val forms = result.body!!.formData

        assertEquals(7, forms.size)
    }

    @Test
    fun `by id`() {
        val result = rest.getForEntity("/v1/form-data?id=10011&id=10012&id=11013", SearchFormDataResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formData = result.body!!.formData
        assertEquals(3, formData.size)
    }

    @Test
    fun `filter by instance-id`() {
        val result =
            rest.getForEntity("/v1/form-data?workflow-instance-id=wi-100", SearchFormDataResponse::class.java)

        val formData = result.body!!.formData
        assertEquals(1, formData.size)
    }

    @Test
    fun `by status`() {
        val result =
            rest.getForEntity("/v1/form-data?status=IN_PROGRESS", SearchFormDataResponse::class.java)

        val formData = result.body!!.formData
        assertEquals(3, formData.size)
    }

    @Test
    fun `form of another tenant`() {
        val result =
            rest.getForEntity("/v1/forms?id=20022", SearchFormDataResponse::class.java)

        assertEquals(0, result.body!!.formData.size)
    }
}
