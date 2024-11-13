package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.dto.UpdateFormDataRequest
import com.wutsi.koki.form.server.dao.FormDataRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/UpdateFormDataEndpoint.sql"])
class UpdateFormDataEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FormDataRepository

    private val request = UpdateFormDataRequest(
        data = mapOf(
            "A" to "aa1",
            "B" to "bb1"
        )
    )

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/form-data/10011", request, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formData = dao.findById("10011").get()
        assertEquals("{\"A\": \"aa1\", \"B\": \"bb1\"}", formData.data)
    }

    @Test
    fun `not found`() {
        val result = rest.postForEntity("/v1/form-data/xx", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.postForEntity("/v1/form-data/20022", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.FORM_DATA_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another user`() {
        val result = rest.postForEntity("/v1/form-data/10012", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
        assertEquals(ErrorCode.AUTHORIZATION_PERMISSION_DENIED, result.body?.error?.code)
    }
}
