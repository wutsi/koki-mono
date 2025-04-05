package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.contact.dto.CreateContactRequest
import com.wutsi.koki.contact.dto.CreateContactResponse
import com.wutsi.koki.contact.dto.Gender
import com.wutsi.koki.contact.server.dao.ContactRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/contact/CreateContactEndpoint.sql"])
class CreateContactEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ContactRepository

    private val request = CreateContactRequest(
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
        language = "fr",
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/contacts", request, CreateContactResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val contactId = response.body!!.contactId
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
        assertEquals(USER_ID, contact.createdById)
        assertEquals(USER_ID, contact.modifiedById)
        assertFalse(contact.deleted)
        assertNull(contact.deletedById)
    }
}
