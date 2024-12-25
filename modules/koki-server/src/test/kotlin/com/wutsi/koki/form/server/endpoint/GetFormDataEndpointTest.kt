package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.dto.FormDataStatus
import com.wutsi.koki.form.dto.GetFormDataResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/GetFormDataEndpoint.sql"])
class GetFormDataEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/form-data/10011", GetFormDataResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formData = result.body!!.formData
        assertEquals("100", formData.formId)
        assertEquals(FormDataStatus.SUBMITTED, formData.status)
        assertEquals("wi-100", formData.workflowInstanceId)
        assertEquals(2, formData.data.size)
        assertEquals("aa", formData.data["A"])
        assertEquals("bb", formData.data["B"])
    }

    @Test
    fun deleted() {
        val result = rest.getForEntity("/v1/form-data/199", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `not found`() {
        val result = rest.getForEntity("/v1/form-data/xxx", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/form-data/20022", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }
}
