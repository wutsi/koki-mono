package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.account.dto.CreateUserRequest
import com.wutsi.koki.account.server.dao.AccountUserRepository
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.UserStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/account/SaveAccountUserEndpoint.sql"])
class SaveAccountUserEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AccountUserRepository

    val request = CreateUserRequest(
        username = "ray.sponsible",
        password = "secret",
        status = UserStatus.ACTIVE,
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/accounts/100/user", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val user = dao.findByUsernameAndTenantId(request.username, TENANT_ID)
        assertEquals(request.username, user?.username)
        assertEquals(request.status, user?.status)
        assertNotNull(user?.password)
        assertNotNull(user?.salt)
    }

    @Test
    fun `create - duplicate name`() {
        val response = rest.postForEntity(
            "/v1/accounts/110/user",
            request.copy(username = "roger.milla"),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_USERNAME, response.body?.error?.code)
    }

    @Test
    fun update() {
        val xrequest = request.copy(username = "omam.mbiyick", status = UserStatus.TERMINATED)
        val response = rest.postForEntity("/v1/accounts/120/user", xrequest, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val user = dao.findByUsernameAndTenantId(xrequest.username, TENANT_ID)
        assertEquals(xrequest.username, user?.username)
        assertEquals(xrequest.status, user?.status)
        assertNotNull(user?.password)
    }

    @Test
    fun `update - duplicate name`() {
        val response = rest.postForEntity(
            "/v1/accounts/110/user",
            request.copy(username = "james.bond"),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_USERNAME, response.body?.error?.code)
    }
}
