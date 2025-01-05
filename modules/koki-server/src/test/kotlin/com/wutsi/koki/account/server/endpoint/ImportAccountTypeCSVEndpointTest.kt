package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.account.dto.GetAccountTypeResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/account/GetAccountTypeEndpoint.sql"])
class GetAccountTypeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/account-types/100", GetAccountTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val account = result.body!!.accountType
        assertEquals("a", account.name)
        assertEquals("title-a", account.title)
        assertEquals("description-a", account.description)
    }

    @Test
    fun `bad id`() {
        val result = rest.getForEntity("/v1/account-types/99999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.ACCOUNT_TYPE_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/account-types/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.ACCOUNT_TYPE_NOT_FOUND, result.body?.error?.code)
    }
}
