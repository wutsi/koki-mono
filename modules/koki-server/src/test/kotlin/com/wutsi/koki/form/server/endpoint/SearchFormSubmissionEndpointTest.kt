package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.dto.GetFormSubmissionResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/GetFormSubmissionEndpoint.sql"])
class GetFormSubmissionEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/form-submissions/10011", GetFormSubmissionResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val submission = result.body!!.formSubmission
        assertEquals("100", submission.formId)
        assertEquals("wi-100", submission.workflowInstanceId)
        assertEquals("wi-100-01", submission.activityInstanceId)
        assertEquals(11, submission.submittedById)
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/form-submissions/xxxx", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.FORM_SUBMISSION_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `form of another tenant`() {
        val result = rest.getForEntity("/v1/form-submissions/20011", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.FORM_SUBMISSION_NOT_FOUND, result.body!!.error.code)
    }
}
