package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.tenant.dto.UpdateUserPhotoRequest
import com.wutsi.koki.tenant.server.dao.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/UpdateUserPhotoEndpoint.sql"])
class UpdateUserPhotoEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: UserRepository

    @Test
    fun set() {
        val request = UpdateUserPhotoRequest(
            photoUrl = "https://random.img/111.png"
        )

        val result = rest.postForEntity("/v1/users/11/photo", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = 11L
        val user = dao.findById(userId).get()
        assertEquals(request.photoUrl, user.photoUrl)
    }

    @Test
    fun reset() {
        val request = UpdateUserPhotoRequest(
            photoUrl = null
        )

        val result = rest.postForEntity("/v1/users/12/photo", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val userId = 12L
        val user = dao.findById(userId).get()
        assertEquals(null, user.photoUrl)
    }
}
