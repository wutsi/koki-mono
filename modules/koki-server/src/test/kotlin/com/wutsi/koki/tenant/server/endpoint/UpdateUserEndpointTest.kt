package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.server.dao.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/UpdateUserEndpoint.sql"])
class UpdateUserEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: UserRepository

    @Autowired
    protected lateinit var ds: DataSource

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
    fun update() {
        val request = UpdateUserRequest(
            email = "thomas.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            roleIds = listOf(11L, 12L),
            language = "fR",
            employer = "Koki",
            categoryId = 111L,
            mobile = "+15147581111",
            country = "CA",
            cityId = 3333L
        )

        val result = rest.postForEntity("/v1/users/11", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = 11L
        val user = dao.findById(userId).get()
        assertEquals(request.displayName, user.displayName)
        assertEquals(request.email?.lowercase(), user.email)
        assertEquals(request.language?.lowercase(), user.language)
        assertEquals(request.categoryId, user.categoryId)
        assertEquals(request.employer?.uppercase(), user.employer)
        assertEquals(request.mobile, user.mobile)
        assertEquals(request.country?.lowercase(), user.country)
        assertEquals(request.cityId, user.cityId)
        assertEquals(request.roleIds?.size, roleCount(userId))
    }

    @Test
    fun `not found`() {
        val request = UpdateUserRequest(
            email = "foo.bar@gmail.com",
            displayName = "Foo Bar",
        )

        val result = rest.postForEntity("/v1/users/99", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `duplicate email`() {
        val request = UpdateUserRequest(
            email = "RAY.sponsible@gmail.com",
            displayName = "Duplicate",
        )

        val result = rest.postForEntity("/v1/users/12", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_EMAIL, result.body!!.error.code)
    }

    @Test
    fun `update user of another tenant`() {
        val request = UpdateUserRequest(
            email = "foo.bar@gmail.com",
            displayName = "Foo Bar",
        )

        val result = rest.postForEntity("/v1/users/22", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body!!.error.code)
    }
}
