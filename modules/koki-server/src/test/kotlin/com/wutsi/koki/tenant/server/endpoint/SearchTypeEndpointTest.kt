package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.contact.dto.SearchContactTypeResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/contact/SearchContactTypeEndpoint.sql"])
class SearchContactTypeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/contact-types", SearchContactTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val contacts = result.body!!.contactTypes
        assertEquals(4, contacts.size)
    }

    @Test
    fun `by name`() {
        val result = rest.getForEntity("/v1/contact-types?name=T1&name=T2", SearchContactTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val contacts = result.body!!.contactTypes
        assertEquals(2, contacts.size)
        assertEquals(100L, contacts[0].id)
        assertEquals(101L, contacts[1].id)
    }

    @Test
    fun `by id`() {
        val result =
            rest.getForEntity("/v1/contact-types?id=103&id=100&id=101", SearchContactTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val contacts = result.body!!.contactTypes
        assertEquals(3, contacts.size)
        assertEquals(100L, contacts[0].id)
        assertEquals(101L, contacts[1].id)
        assertEquals(103L, contacts[2].id)
    }

    @Test
    fun active() {
        val result = rest.getForEntity("/v1/contact-types?active=false", SearchContactTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val contacts = result.body!!.contactTypes
        assertEquals(1, contacts.size)
        assertEquals(103L, contacts[0].id)
    }
}
