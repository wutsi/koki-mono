package com.wutsi.koki.tenant.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.CreateUserResponse
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.dao.UserRepository
import com.wutsi.koki.tenant.server.service.PasswordService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/CreateUserEndpoint.sql"])
class CreateUserEndpointTest : TenantAwareEndpointTest() {
    companion object {
        const val HASHED_PASSWORD = "607e0b9e5496964b1385b7c10e3e2403"
    }

    @Autowired
    private lateinit var dao: UserRepository

    @MockBean
    private lateinit var passwordService: PasswordService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doReturn(HASHED_PASSWORD).whenever(passwordService).hash(any(), any())
    }

    @Test
    fun create() {
        val request = CreateUserRequest(
            email = "thomas.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            password = "secret"
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.displayName, user.displayName)
        assertEquals(request.email, user.email)
        assertEquals(UserStatus.ACTIVE, user.status)
        assertEquals(36, user.salt.length)
        assertEquals(HASHED_PASSWORD, user.password)
        assertEquals(TENANT_ID, user.tenantId)

        verify(passwordService).hash(request.password, user.salt)
    }

    @Test
    fun `email is saved in lowercase`() {
        val request = CreateUserRequest(
            email = "OMAM.MBIYICK@hotmail.com",
            displayName = "Omam Mbiyick",
            password = "secret"
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.email.lowercase(), user.email)
    }

    @Test
    fun `duplicate email`() {
        val request = CreateUserRequest(
            email = "RAY.sponsible@gmail.com",
            displayName = "Ray",
            password = "secret"
        )

        val result = rest.postForEntity("/v1/users", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_EMAIL, result.body!!.error.code)
    }

    @Test
    fun `same user on multiple tenant`() {
        val request = CreateUserRequest(
            email = "roger.milla@gmail.com",
            displayName = "Roger Milla",
            password = "secret"
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.displayName, user.displayName)
        assertEquals(request.email, user.email)
        assertEquals(UserStatus.ACTIVE, user.status)
        assertEquals(36, user.salt.length)
        assertEquals(HASHED_PASSWORD, user.password)
        assertEquals(TENANT_ID, user.tenantId)

        verify(passwordService).hash(request.password, user.salt)
    }
}
