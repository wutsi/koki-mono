package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.dto.GetFormDataResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/GetFormDataEndpoint.sql"])
class GetFormDataEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/form-data/100", GetFormDataResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formData = result.body!!.formData
        assertEquals(2, formData.data.size)
        assertEquals("value1", formData.data["var1"])
        assertEquals("value2", formData.data["var2"])
    }

    @Test
    fun `data not found`() {
        val result = rest.getForEntity("/v1/form-data/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `get data from another tenant`() {
        val result = rest.getForEntity("/v1/form-data/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }
}
