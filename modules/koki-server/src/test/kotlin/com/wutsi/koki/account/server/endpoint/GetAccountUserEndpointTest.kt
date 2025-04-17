package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.account.dto.GetAccountUserResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.UserStatus
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/account/GetAccountUserEndpoint.sql"])
class GetAccountUserEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/account-users/101", GetAccountUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accountUser = result.body!!.accountUser
        assertEquals("roger.milla", accountUser.username)
        assertEquals(100L, accountUser.accountId)
        assertEquals(UserStatus.SUSPENDED, accountUser.status)
    }

    @Test
    fun `bad id`() {
        val result = rest.getForEntity("/v1/account-users/99999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/account-users/201", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }
}
