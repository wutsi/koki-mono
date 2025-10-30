package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.UpdateUserProfileRequest
import com.wutsi.koki.tenant.server.dao.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/UpdateUserProfileEndpoint.sql"])
class UpdateUserProfileEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: UserRepository

    @Test
    fun update() {
        val request = UpdateUserProfileRequest(
            email = "thomas.nkono@hotmail.com",
            displayName = "Thomas Nkono",
            language = "fR",
            employer = "Koki",
            categoryId = 111L,
            mobile = "+15147581111",
            country = "CA",
            cityId = 3333L,
            biography = "This is the nice biography",
            facebookUrl = "https://www.facebook.com/user/thomas.knono",
            instagramUrl = "https://www.instagram.com/user/thomas.knono",
            tiktokUrl = "https://www.tiktok.com/user/thomas.knono",
            youtubeUrl = "https://www.youtube.com/user/thomas.knono",
            twitterUrl = "https://www.x.com/user/thomas.knono",
            websiteUrl = "https://koki.com/agent/1320943"
        )

        val result = rest.postForEntity("/v1/users/11/profile", request, Any::class.java)

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
        assertEquals(request.biography, user.biography)
        assertEquals(request.facebookUrl, user.facebookUrl)
        assertEquals(request.instagramUrl, user.instagramUrl)
        assertEquals(request.twitterUrl, user.twitterUrl)
        assertEquals(request.tiktokUrl, user.tiktokUrl)
        assertEquals(request.youtubeUrl, user.youtubeUrl)
        assertEquals(request.websiteUrl, user.websiteUrl)
    }

    @Test
    fun `duplicate email`() {
        val request = UpdateUserProfileRequest(
            email = "john.smith@gmail.com",
            displayName = "Duplicate",
        )

        val result = rest.postForEntity("/v1/users/11/profile", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.USER_DUPLICATE_EMAIL, result.body!!.error.code)
    }

    @Test
    fun `not found`() {
        val request = UpdateUserProfileRequest(
            email = "foo.bar@gmail.com",
            displayName = "Foo Bar",
        )

        val result = rest.postForEntity("/v1/users/99/profile", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body!!.error.code)
    }
}
