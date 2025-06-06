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
import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.dao.UserRepository
import com.wutsi.koki.tenant.server.service.PasswordService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import javax.sql.DataSource
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/CreateUserEndpoint.sql"])
class CreateUserEndpointTest : TenantAwareEndpointTest() {
    companion object {
        const val HASHED_PASSWORD = "607e0b9e5496964b1385b7c10e3e2403"
    }

    @Autowired
    private lateinit var dao: UserRepository

    @MockitoBean
    private lateinit var passwordService: PasswordService

    @Autowired
    protected lateinit var ds: DataSource

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doReturn(HASHED_PASSWORD).whenever(passwordService).hash(any(), any())
    }

    private fun roleCount(userId: Long): Int {
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery("SELECT count(*) FROM T_USER_ROLE where user_fk=$userId")
                rs.use {
                    if (rs.next()) {
                        return rs.getInt(1)
                    }
                }
            }
        }
        return -1
    }

    @Test
    fun create() {
        val request = CreateUserRequest(
            username = "thomas.nkono",
            email = "thomas.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            password = "secret",
            roleIds = listOf(11L, 12L),
            language = "fr",
            type = UserType.EMPLOYEE,
            status = UserStatus.ACTIVE,
            accountId = null,
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.displayName, user.displayName)
        assertEquals(request.username, user.username)
        assertEquals(request.email, user.email)
        assertEquals(request.language, user.language)
        assertEquals(request.status, user.status)
        assertEquals(request.type, user.type)
        assertEquals(36, user.salt.length)
        assertEquals(HASHED_PASSWORD, user.password)
        assertEquals(TENANT_ID, user.tenantId)
        assertEquals(request.roleIds.size, roleCount(userId))
        assertEquals(request.accountId, user.accountId)

        verify(passwordService).hash(request.password, user.salt)
    }

    @Test
    fun `create account user`() {
        val request = CreateUserRequest(
            username = "thomas2.nkono",
            email = "thomas.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            password = "secret",
            roleIds = listOf(11L, 12L),
            language = "fr",
            type = UserType.ACCOUNT,
            status = UserStatus.ACTIVE,
            accountId = 333L,
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.displayName, user.displayName)
        assertEquals(request.username, user.username)
        assertEquals(request.email, user.email)
        assertEquals(request.language, user.language)
        assertEquals(request.status, user.status)
        assertEquals(request.type, user.type)
        assertEquals(36, user.salt.length)
        assertEquals(HASHED_PASSWORD, user.password)
        assertEquals(TENANT_ID, user.tenantId)
        assertEquals(request.roleIds.size, roleCount(userId))
        assertEquals(request.accountId, user.accountId)

        verify(passwordService).hash(request.password, user.salt)
    }

    @Test
    fun `username and email is saved in lowercase`() {
        val request = CreateUserRequest(
            username = "OMAM.MBIYICK",
            email = "OMAM.mbiyick@hotmail.com",
            displayName = "Omam Mbiyick",
            password = "secret",
            type = UserType.EMPLOYEE,
            status = UserStatus.ACTIVE,
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.username.lowercase(), user.username)
        assertEquals(request.email.lowercase(), user.email)
    }

    @Test
    fun `duplicate username`() {
        val request = CreateUserRequest(
            username = "RAY.sponsible",
            email = "RAY00.sponsible@gmail.com",
            displayName = "Ray",
            password = "secret",
            type = UserType.EMPLOYEE,
            status = UserStatus.ACTIVE,
        )

        val result = rest.postForEntity("/v1/users", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_USERNAME, result.body!!.error.code)
    }

    @Test
    fun `duplicate email`() {
        val request = CreateUserRequest(
            username = "RAY00.sponsible",
            email = "RAY.sponsible@gmail.com",
            displayName = "Ray",
            password = "secret",
            type = UserType.EMPLOYEE,
            status = UserStatus.ACTIVE,
        )

        val result = rest.postForEntity("/v1/users", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_EMAIL, result.body!!.error.code)
    }

    @Test
    fun `missing account-id`() {
        val request = CreateUserRequest(
            username = "RAY00.sponsible",
            email = "RAY.sponsible@gmail.com",
            displayName = "Ray",
            password = "secret",
            type = UserType.ACCOUNT,
            status = UserStatus.ACTIVE,
            accountId = null,
        )

        val result = rest.postForEntity("/v1/users", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.USER_ACCOUNT_ID_MISSING, result.body!!.error.code)
    }

    @Test
    fun `invalid account-id`() {
        val username = "roger." + UUID.randomUUID()
        val request = CreateUserRequest(
            username = username,
            email = "$username@gmail.com",
            displayName = "Ray",
            password = "secret",
            type = UserType.EMPLOYEE,
            status = UserStatus.ACTIVE,
            accountId = 333L,
        )

        val result = rest.postForEntity("/v1/users", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals(ErrorCode.USER_ACCOUNT_ID_SHOULD_BE_NULL, result.body!!.error.code)
    }

    @Test
    fun `same user on multiple tenant`() {
        val request = CreateUserRequest(
            username = "roger.milla",
            email = "roger.milla@gmail.com",
            displayName = "Roger Milla",
            password = "secret",
            type = UserType.EMPLOYEE,
            status = UserStatus.SUSPENDED,
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.username, user.username)
        assertEquals(request.displayName, user.displayName)
        assertEquals(request.email, user.email)
        assertEquals(request.status, user.status)
        assertEquals(request.type, user.type)
        assertEquals(36, user.salt.length)
        assertEquals(HASHED_PASSWORD, user.password)
        assertEquals(TENANT_ID, user.tenantId)
        assertEquals(request.language, user.language)

        verify(passwordService).hash(request.password, user.salt)
    }
}
