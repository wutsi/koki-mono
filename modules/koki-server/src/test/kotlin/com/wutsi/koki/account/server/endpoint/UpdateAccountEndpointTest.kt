package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.account.dto.UpdateAccountRequest
import com.wutsi.koki.account.server.dao.AccountAttributeRepository
import com.wutsi.koki.account.server.dao.AccountRepository
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/account/UpdateAccountEndpoint.sql"])
class UpdateAccountEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AccountRepository

    @Autowired
    private lateinit var attrDao: AccountAttributeRepository

    private val request = UpdateAccountRequest(
        accountTypeId = 100L,
        name = "Ray Sponsible Inc",
        phone = "+5141110000",
        mobile = "+5141110011",
        email = "info@ray-sponsible-inc.com",
        website = "https://www.ray-sponsible-inc.com",
        language = "en",
        description = "Description of the account",
        attributes = mapOf(
            100L to "NEQ-0000001",
            101L to "",
            102L to "TVQ-22222",
            104L to "yes"
        )
    )

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/accounts/1000", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val accountId = 1000L
        val account = dao.findById(accountId).get()
        assertEquals(request.accountTypeId, account.accountTypeId)
        assertEquals(request.name, account.name)
        assertEquals(request.description, account.description)
        assertEquals(request.phone, account.phone)
        assertEquals(request.mobile, account.mobile)
        assertEquals(request.email, account.email)
        assertEquals(request.website, account.website)
        assertEquals(request.language, account.language)
        assertEquals(USER_ID, account.modifiedById)

        val attrs = attrDao.findByAccountId(accountId).sortedBy { it.attributeId }
        assertEquals(5, attrs.size)

        assertEquals(accountId, attrs[0].accountId)
        assertEquals(100L, attrs[0].attributeId)
        assertEquals(request.attributes[100], attrs[0].value)

        assertEquals(accountId, attrs[1].accountId)
        assertEquals(101L, attrs[1].attributeId)
        assertNull(attrs[1].value)

        assertEquals(accountId, attrs[2].accountId)
        assertEquals(102L, attrs[2].attributeId)
        assertEquals(request.attributes[102], attrs[2].value)

        assertEquals(accountId, attrs[3].accountId)
        assertEquals(103L, attrs[3].attributeId)
        assertNull(attrs[3].value)

        assertEquals(accountId, attrs[4].accountId)
        assertEquals(104L, attrs[4].attributeId)
        assertEquals(request.attributes[104], attrs[4].value)
    }

    @Test
    fun deleted() {
        val response = rest.postForEntity("/v1/accounts/1999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `not found`() {
        val response = rest.postForEntity("/v1/accounts/9999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val response = rest.postForEntity("/v1/accounts/2000", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, response.body?.error?.code)
    }
}
