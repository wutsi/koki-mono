package com.wutsi.koki.tenant.server.server.endpoint

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
    fun create() {
        val result = rest.getForEntity("/v1/forms/100", GetFormResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val form = result.body!!.form
        assertEquals("Form 100", form.title)
        assertEquals("Sample Form", form.content.title)
        assertEquals("Description of the form", form.content.description)
    }

    @Test
    fun `update workflow not found`() {
        val result = rest.getForEntity("/v1/forms/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `update workflow of another tenant`() {
        val result = rest.getForEntity("/v1/forms/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_NOT_FOUND, result.body?.error?.code)
    }
}
