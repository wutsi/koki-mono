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
import com.wutsi.koki.tenant.dto.InvitationStatus
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.dao.InvitationRepository
import com.wutsi.koki.tenant.server.dao.UserRepository
import com.wutsi.koki.tenant.server.service.PasswordEncryptor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
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

    @Autowired
    private lateinit var invitationDao: InvitationRepository

    @MockitoBean
    private lateinit var passwordEncryptor: PasswordEncryptor

    @Autowired
    protected lateinit var ds: DataSource

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doReturn(HASHED_PASSWORD).whenever(passwordEncryptor).hash(any(), any())
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

    private fun roleIds(userId: Long): List<Long> {
        val cnn = ds.connection
        val result = mutableListOf<Long>()
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery("SELECT role_fk FROM T_USER_ROLE where user_fk=$userId")
                rs.use {
                    if (rs.next()) {
                        result.add(rs.getLong(1))
                    }
                }
            }
        }
        return result
    }

    @Test
    fun create() {
        val request = CreateUserRequest(
            username = "thomas.nkono",
            email = "THOMAS.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            password = "secret",
            roleIds = listOf(11L, 12L),
            language = "Fr",
            employer = "Koki",
            categoryId = 111L,
            mobile = "+15147581111",
            country = "CA",
            cityId = 3333L,
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.displayName, user.displayName)
        assertEquals(request.username, user.username)
        assertEquals(request.email?.lowercase(), user.email)
        assertEquals(request.language?.lowercase(), user.language)
        assertEquals(36, user.salt.length)
        assertEquals(HASHED_PASSWORD, user.password)
        assertEquals(TENANT_ID, user.tenantId)
        assertEquals(UserStatus.ACTIVE, user.status)
        assertEquals(request.categoryId, user.categoryId)
        assertEquals(request.employer?.uppercase(), user.employer)
        assertEquals(request.mobile, user.mobile)
        assertEquals(request.roleIds.size, roleCount(userId))
        assertEquals(request.country?.lowercase(), user.country)
        assertEquals(request.cityId, user.cityId)
        assertEquals(null, user.invitationId)

        verify(passwordEncryptor).hash(request.password, user.salt)
    }

    @Test
    fun `duplicate username`() {
        val request = CreateUserRequest(
            username = "RAY.sponsible",
            email = null,
            password = "secret",
        )

        val result = rest.postForEntity("/v1/users", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_USERNAME, result.body!!.error.code)
    }

    @Test
    fun `duplicate email`() {
        val request = CreateUserRequest(
            username = "YO.MAN",
            email = "ray.sponsible@gmail.com",
            password = "secret",
        )

        val result = rest.postForEntity("/v1/users", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_EMAIL, result.body!!.error.code)
    }

    @Test
    fun `same user on multiple tenant`() {
        val request = CreateUserRequest(
            username = "roger.milla",
            email = "roger.milla@gmail.com",
            displayName = "Roger Milla",
            password = "secret",
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.username, user.username)
        assertEquals(request.displayName, user.displayName)
        assertEquals(request.email, user.email)
        assertEquals(36, user.salt.length)
        assertEquals(HASHED_PASSWORD, user.password)
        assertEquals(TENANT_ID, user.tenantId)
        assertEquals(request.language, user.language)

        verify(passwordEncryptor).hash(request.password, user.salt)
    }

    @Test
    fun `invite agent`() {
        val request = CreateUserRequest(
            username = "invited.nkono",
            email = "invited.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            password = "secret",
            invitationId = "100"
        )

        val result = rest.postForEntity("/v1/users", request, CreateUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = result.body!!.userId
        val user = dao.findById(userId).get()
        assertEquals(request.invitationId, user.invitationId)
        assertEquals(listOf(15L), roleIds(userId))
        assertNotNull(user.invitationId)

        val invitation = invitationDao.findById(user.invitationId).get()
        assertEquals(InvitationStatus.ACCEPTED, invitation.status)
        assertNotNull(invitation.acceptedAt)
    }

    @Test
    fun `invite with invitation already accepted`() {
        val request = CreateUserRequest(
            username = "invited.nkono",
            email = "accepted.invitation.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            password = "secret",
            invitationId = "101"
        )

        val result = rest.postForEntity("/v1/users", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.INVITATION_ALREADY_ACCEPTED, result.body!!.error.code)
    }

    @Test
    fun `invite with expired invitation`() {
        val request = CreateUserRequest(
            username = "invited.nkono",
            email = "expired.invitation.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            password = "secret",
            invitationId = "102"
        )

        val result = rest.postForEntity("/v1/users", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.INVITATION_EXPIRED, result.body!!.error.code)
    }
}
