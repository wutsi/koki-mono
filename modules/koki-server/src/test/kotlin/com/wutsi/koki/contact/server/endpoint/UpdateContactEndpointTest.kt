package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.contact.dto.Gender
import com.wutsi.koki.contact.dto.UpdateContactRequest
import com.wutsi.koki.contact.server.dao.ContactRepository
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/contact/UpdateContactEndpoint.sql"])
class UpdateContactEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ContactRepository

    private val request = UpdateContactRequest(
        contactTypeId = 100L,
        accountId = 1000L,
        salutations = "Mr",
        firstName = "Ray",
        lastName = "Sponsible",
        phone = "+5141110000",
        mobile = "+5141110011",
        email = "info@ray-sponsible-inc.com",
        profession = "Director",
        employer = "Google",
        gender = Gender.MALE,
        language = "fr"
    )

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/contacts/100", request, UpdateContactRequest::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val contactId = 100L
        val contact = dao.findById(contactId).get()
        assertEquals(request.contactTypeId, contact.contactTypeId)
        assertEquals(request.accountId, contact.accountId)
        assertEquals(request.salutations, contact.salutation)
        assertEquals(request.firstName, contact.firstName)
        assertEquals(request.lastName, contact.lastName)
        assertEquals(request.phone, contact.phone)
        assertEquals(request.mobile, contact.mobile)
        assertEquals(request.employer, contact.employer)
        assertEquals(request.profession, contact.profession)
        assertEquals(request.employer, contact.employer)
        assertEquals(request.gender, contact.gender)
        assertEquals(request.language, contact.language)
        assertEquals(USER_ID, contact.modifiedById)
        assertFalse(contact.deleted)
        assertNull(contact.deletedById)
    }

    @Test
    fun deleted() {
        val response = rest.postForEntity("/v1/contacts/199", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.CONTACT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `not found`() {
        val response = rest.postForEntity("/v1/contacts/199", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.CONTACT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val response = rest.postForEntity("/v1/contacts/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.CONTACT_NOT_FOUND, response.body?.error?.code)
    }
}
