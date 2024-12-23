package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.form.dto.SearchFormSubmissionResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/SearchFormSubmissionEndpoint.sql"])
class SearchFormSubmissionEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/form-submissions", SearchFormSubmissionResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val submissions = result.body!!.formSubmissions
        assertEquals(4, submissions.size)
    }

    @Test
    fun `by formId`() {
        val result = rest.getForEntity("/v1/form-submissions?form-id=110", SearchFormSubmissionResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val submissions = result.body!!.formSubmissions
        assertEquals(1, submissions.size)
    }
}
