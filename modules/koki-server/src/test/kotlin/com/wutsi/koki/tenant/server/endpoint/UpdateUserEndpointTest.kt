package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.dao.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/UpdateUserEndpoint.sql"])
class UpdateUserEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: UserRepository

    @Test
    fun update() {
        val request = UpdateUserRequest(
            email = "thomas.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            status = UserStatus.TERMINATED,
        )

        val result = rest.postForEntity("/v1/users/11", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = 11L
        val user = dao.findById(userId).get()
        assertEquals(request.displayName, user.displayName)
        assertEquals(request.email, user.email)
        assertEquals(request.status, user.status)
    }

    @Test
    fun `email is saved in lowercase`() {
        val request = UpdateUserRequest(
            email = "OMAM.MBIYICK@hotmail.com",
            displayName = "Omam Mbiyick",
            status = UserStatus.TERMINATED,
        )

        val result = rest.postForEntity("/v1/users/11", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = 11L
        val user = dao.findById(userId).get()
        assertEquals(request.email.lowercase(), user.email)
    }

    @Test
    fun `duplicate email`() {
        val request = UpdateUserRequest(
            email = "RAY.sponsible@gmail.com",
            displayName = "Duplicate",
            status = UserStatus.TERMINATED,
        )

        val result = rest.postForEntity("/v1/users/12", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_EMAIL, result.body!!.error.code)
    }

    @Test
    fun `not found`() {
        val request = UpdateUserRequest(
            email = "foo.bar@gmail.com",
            displayName = "Foo Bar",
            status = UserStatus.TERMINATED,
        )

        val result = rest.postForEntity("/v1/users/99", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `update user of another tenant`() {
        val request = UpdateUserRequest(
            email = "foo.bar@gmail.com",
            displayName = "Foo Bar",
            status = UserStatus.TERMINATED,
        )

        val result = rest.postForEntity("/v1/users/22", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body!!.error.code)
    }
}
