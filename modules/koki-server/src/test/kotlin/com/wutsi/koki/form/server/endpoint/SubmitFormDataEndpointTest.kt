package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.dto.SubmitFormDataRequest
import com.wutsi.koki.form.dto.SubmitFormDataResponse
import com.wutsi.koki.form.server.dao.FormDataRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/CreateFormDataEndpoint.sql"])
class CreateFormDataEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FormDataRepository

    private val request = SubmitFormDataRequest(
        formId = "100",
        data = mapOf(
            "A" to "aa",
            "B" to "bb"
        ),
        status = FormDataStatus.PENDING,
    )

    @Test
    fun create() {
        val result = rest.postForEntity("/v1/form-data", request, SubmitFormDataResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val formDataId = result.body!!.formDataId
        val formData = dao.findById(formDataId).get()
        assertEquals(USER_ID, formData.userId)
        assertEquals(request.formId, formData.form.id)
        assertEquals(request.status, formData.status)
        assertEquals("{\"A\": \"aa\", \"B\": \"bb\"}", formData.data)
    }

    @Test
    fun `no user`() {
        ignoreAuthHeader = true

        val result = rest.postForEntity("/v1/form-data", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }
}
