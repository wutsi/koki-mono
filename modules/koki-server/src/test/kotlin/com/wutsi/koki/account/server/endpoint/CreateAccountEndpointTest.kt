package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.account.dto.CreateAccountRequest
import com.wutsi.koki.account.dto.CreateAccountResponse
import com.wutsi.koki.account.server.dao.AccountAttributeRepository
import com.wutsi.koki.account.server.dao.AccountRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/account/CreateAccountEndpoint.sql"])
class CreateAccountEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AccountRepository

    @Autowired
    private lateinit var attrDao: AccountAttributeRepository

    @Test
    fun create() {
        val request = CreateAccountRequest(
            name = "Ray Sponsible Inc",
            phone = "+5141110000",
            mobile = "+5141110011",
            email = "info@ray-sponsible-inc.com",
            website = "https://www.ray-sponsible-inc.com",
            language = "en",
            description = "Description of the account",
            attributes = mapOf(
                100L to "NEQ-0000001",
                101L to "40394039"
            ),
            accountTypeId = 100L,
        )
        val response = rest.postForEntity("/v1/accounts", request, CreateAccountResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val accountId = response.body!!.accountId
        val account = dao.findById(accountId).get()
        assertEquals(request.accountTypeId, account.accountTypeId)
        assertEquals(request.name, account.name)
        assertEquals(request.description, account.description)
        assertEquals(request.phone, account.phone)
        assertEquals(request.mobile, account.mobile)
        assertEquals(request.email, account.email)
        assertEquals(request.website, account.website)
        assertEquals(request.language, account.language)
        assertEquals(USER_ID, account.createdById)
        assertEquals(USER_ID, account.modifiedById)
        assertFalse(account.deleted)
        assertNull(account.deletedAt)
        assertNull(account.deletedById)

        val attrs = attrDao.findByAccountId(accountId).sortedBy { it.attributeId }
        assertEquals(2, attrs.size)

        assertEquals(accountId, attrs[0].accountId)
        assertEquals(100L, attrs[0].attributeId)
        assertEquals(request.attributes[100], attrs[0].value)

        assertEquals(accountId, attrs[1].accountId)
        assertEquals(101L, attrs[1].attributeId)
        assertEquals(request.attributes[101], attrs[1].value)
    }
}
