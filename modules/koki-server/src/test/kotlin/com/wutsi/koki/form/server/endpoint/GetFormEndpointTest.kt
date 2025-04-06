package com.wutsi.koki.form.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.dto.GetFormResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/GetFormEndpoint.sql"])
class GetFormEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/forms/100", GetFormResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val form = result.body!!.form
        assertEquals("T-100", form.code)
        assertEquals("f-100", form.name)
        assertEquals("This is the F-100 form", form.description)
        assertEquals(false, form.active)
    }

    @Test
    fun deleted() {
        val result = rest.getForEntity("/v1/forms/199", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `get form with invalid id`() {
        val result = rest.getForEntity("/v1/forms/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `get form of another tenant`() {
        val result = rest.getForEntity("/v1/forms/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_NOT_FOUND, result.body?.error?.code)
    }
}
