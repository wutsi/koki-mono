package com.wutsi.koki.account.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.account.dto.CreateUserRequest
import com.wutsi.koki.account.dto.CreateUserResponse
import com.wutsi.koki.account.server.dao.AccountRepository
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.dao.UserRepository
import com.wutsi.koki.tenant.server.service.PasswordService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/account/CreateUserEndpoint.sql"])
class CreateUserEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var accountDao: AccountRepository

    @MockitoBean
    private lateinit var passwordService: PasswordService

    val request = CreateUserRequest(
        username = "ray.sponsible",
        password = "secret",
        status = UserStatus.ACTIVE,
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doReturn("__secret__").whenever(passwordService).hash(any(), any())
    }

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/accounts/100/user", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val account = accountDao.findById(100).get()
        assertEquals(response.body?.userId, account.userId)

        val user = userDao.findById(response.body!!.userId).get()
        assertEquals(TENANT_ID, user.tenantId)
        assertEquals(request.username, user.username)
        assertEquals(request.status, user.status)
        assertEquals("__secret__", user.password)
        assertEquals(account.name, user.displayName)
        assertEquals(account.email, user.email)
        assertEquals(account.language, user.language)
        assertNotNull(user.salt)
    }

    @Test
    fun `account has user`() {
        val response = rest.postForEntity("/v1/accounts/111/user", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(111L, response.body!!.userId)
    }
}
