package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.contact.dto.SearchContactResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/contact/SearchContactEndpoint.sql"])
class SearchContactEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/contacts", SearchContactResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val contacts = response.body!!.contacts
        assertEquals(4, contacts.size)
    }

    @Test
    fun `by keywords`() {
        val response = rest.getForEntity("/v1/contacts?q=RAY", SearchContactResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val contacts = response.body!!.contacts
        assertEquals(2, contacts.size)
        assertEquals(100L, contacts[0].id)
        assertEquals(101L, contacts[1].id)
    }

    @Test
    fun `by account id`() {
        val response = rest.getForEntity("/v1/contacts?account-id=1000", SearchContactResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val contacts = response.body!!.contacts
        assertEquals(2, contacts.size)
        assertEquals(100L, contacts[0].id)
        assertEquals(103L, contacts[1].id)
    }

    @Test
    fun `by contact type`() {
        val response = rest.getForEntity("/v1/contacts?contact-type-id=100", SearchContactResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val contacts = response.body!!.contacts
        assertEquals(2, contacts.size)
        assertEquals(100L, contacts[0].id)
        assertEquals(102L, contacts[1].id)
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/contacts?id=100&id=101&id=200&id=102", SearchContactResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val contacts = response.body!!.contacts
        assertEquals(3, contacts.size)
        assertEquals(100L, contacts[0].id)
        assertEquals(101L, contacts[1].id)
        assertEquals(102L, contacts[2].id)
    }

    @Test
    fun `by account-manager-id`() {
        val response = rest.getForEntity("/v1/contacts?account-manager-id=11", SearchContactResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val contacts = response.body!!.contacts
        assertEquals(2, contacts.size)
        assertEquals(100L, contacts[0].id)
        assertEquals(103L, contacts[1].id)
    }
}
