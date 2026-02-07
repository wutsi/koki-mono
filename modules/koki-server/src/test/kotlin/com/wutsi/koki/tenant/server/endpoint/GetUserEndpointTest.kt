package com.wutsi.koki.tenant.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.GetUserResponse
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.service.UserImageResizer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GetUserEndpoint.sql"])
class GetUserEndpointTest : TenantAwareEndpointTest() {
    @MockitoBean
    private lateinit var resizer: UserImageResizer

    @Test
    fun get() {
        // GIVEN
        doReturn("https://www.resizer.com/tiny.png").whenever(resizer).tinyUrl(any())
        doReturn("https://www.resizer.com/thumbnail.png").whenever(resizer).thumbnailUrl(any())
        doReturn("https://www.resizer.com/preview.png").whenever(resizer).previewUrl(any())
        doReturn("https://www.resizer.com/og.png").whenever(resizer).openGraphUrl(any())

        // WHEN
        val result = rest.getForEntity("/v1/users/11", GetUserResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val user = result.body!!.user
        assertEquals(11L, user.id)
        assertEquals("xxx", user.deviceId)
        assertEquals("Ray Sponsible", user.displayName)
        assertEquals("ray.sponsible", user.username)
        assertEquals("ray.sponsible@gmail.com", user.email)
        assertEquals(UserStatus.ACTIVE, user.status)
        assertEquals("KOKI", user.employer)
        assertEquals("ca", user.country)
        assertEquals(111L, user.cityId)
        assertEquals("fr", user.language)
        assertEquals("3333 Linton", user.street)
        assertEquals("https://img.com/1.png", user.photoUrl)
        assertEquals("https://www.resizer.com/tiny.png", user.photoTinyUrl)
        assertEquals("https://www.resizer.com/thumbnail.png", user.photoThumbnailUrl)
        assertEquals("https://www.resizer.com/preview.png", user.photoPreviewUrl)

        assertEquals(3, user.roleIds.size)
        assertEquals(10, user.roleIds[0])
        assertEquals(11, user.roleIds[1])
        assertEquals(12, user.roleIds[2])

        assertNotNull(user.profileStrength)
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/users/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `user of another tenant`() {
        val result = rest.getForEntity("/v1/users/22", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.USER_NOT_FOUND, result.body?.error?.code)
    }
}
