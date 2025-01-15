package com.wutsi.koki.email.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.email.dto.SearchEmailResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/email/SearchEmailEndpoint.sql"])
class SearchEmailEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/emails", SearchEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val emails = response.body!!.emails
        assertEquals(5, emails.size)
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/emails?id=100&id=200&id=101", SearchEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val emails = response.body!!.emails
        assertEquals(2, emails.size)
        assertEquals("101", emails[0].id)
        assertEquals("100", emails[1].id)
    }

    @Test
    fun `by owner`() {
        val response = rest.getForEntity("/v1/emails?owner-id=111&owner-type=ACCOUNT", SearchEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val emails = response.body!!.emails
        assertEquals(2, emails.size)
        assertEquals("103", emails[0].id)
        assertEquals("100", emails[1].id)
    }
}
