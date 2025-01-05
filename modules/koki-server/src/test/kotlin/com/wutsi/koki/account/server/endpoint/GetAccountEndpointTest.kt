package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/account/GetAccountEndpoint.sql"])
class GetAccountEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/accounts/1000", GetAccountResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val account = result.body!!.account
        assertEquals("Inc", account.name)
        assertEquals("+5147580000", account.phone)
        assertEquals("+5147580011", account.mobile)
        assertEquals("info@inc.com", account.email)
        assertEquals("https://www.inc.com", account.website)
        assertEquals("This is the description of account", account.description)
        assertEquals("fr", account.language)
        assertEquals(11L, account.createdById)
        assertEquals(12L, account.modifiedById)
        assertEquals(13L, account.managedById)

        assertEquals(2, account.attributes.size)
        assertEquals("NEQ-00000", account.attributes[100])
        assertEquals("TPS-11111", account.attributes[101])
    }

    @Test
    fun `bad id`() {
        val result = rest.getForEntity("/v1/accounts/99999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun deleted() {
        val result = rest.getForEntity("/v1/accounts/1999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/accounts/2000", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, result.body?.error?.code)
    }
}
