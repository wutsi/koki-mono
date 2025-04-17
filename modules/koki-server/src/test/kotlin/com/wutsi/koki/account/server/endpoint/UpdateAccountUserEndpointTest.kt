package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.account.dto.UpdateAccountUserRequest
import com.wutsi.koki.account.server.dao.AccountUserRepository
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.UserStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/account/UpdateAccountUserEndpoint.sql"])
class UpdateAccountUserEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AccountUserRepository

    val request = UpdateAccountUserRequest(
        username = "ray.sponsible",
        status = UserStatus.TERMINATED,
    )

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/account-users/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val user = dao.findById(100).get()
        assertEquals(request.username, user.username)
        assertEquals(request.status, user.status)
    }

    @Test
    fun `update - duplicate name`() {
        val response = rest.postForEntity(
            "/v1/account-users/110",
            request.copy(username = "roger.milla"),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_USERNAME, response.body?.error?.code)
    }
}
