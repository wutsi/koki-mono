package com.wutsi.koki.account.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.account.dto.CreateAccountUserRequest
import com.wutsi.koki.account.dto.CreateAccountUserResponse
import com.wutsi.koki.account.server.dao.AccountRepository
import com.wutsi.koki.account.server.dao.AccountUserRepository
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.service.PasswordService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/account/CreateAccountUserEndpoint.sql"])
class CreateAccountUserEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AccountUserRepository

    @Autowired
    private lateinit var accountDao: AccountRepository

    @MockitoBean
    private lateinit var passwordService: PasswordService

    val request = CreateAccountUserRequest(
        accountId = 100,
        username = "ray.sponsible",
        password = "secret",
        status = UserStatus.ACTIVE,
    )

    @Test
    fun create() {
        doReturn("__secret__").whenever(passwordService).hash(any(), any())

        val response = rest.postForEntity("/v1/account-users", request, CreateAccountUserResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val user = dao.findById(response.body!!.accountUserId).get()
        assertEquals(request.username, user.username)
        assertEquals(request.status, user.status)
        assertEquals("__secret__", user?.password)
        assertNotNull(user.salt)

        val account = accountDao.findById(request.accountId).get()
        assertEquals(user.id, account.userId)

        verify(passwordService).hash(request.password, user.salt)
    }

    @Test
    fun `create - duplicate name`() {
        val response = rest.postForEntity(
            "/v1/account-users",
            request.copy(accountId = 110, username = "roger.milla"),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_USERNAME, response.body?.error?.code)
    }

    @Test
    fun `create - invalid account`() {
        val response = rest.postForEntity(
            "/v1/account-users",
            request.copy(accountId = 9999, username = "invalid.accouot"),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, response.body?.error?.code)
    }
}
