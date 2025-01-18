package com.wutsi.koki.module.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.module.dto.SearchPermissionResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/module/SearchPermissionEndpoint.sql"])
class SearchPermissionEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/permissions", SearchPermissionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val permissions = response.body!!.permissions
        assertEquals(5, permissions.size)
    }

    @Test
    fun `by ids`() {
        val response = rest.getForEntity("/v1/permissions?id=101&id=102", SearchPermissionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val permissions = response.body!!.permissions
        assertEquals(2, permissions.size)
    }

    @Test
    fun `by module-id`() {
        val response =
            rest.getForEntity("/v1/permissions?module-id=100&module-id=300", SearchPermissionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val permissions = response.body!!.permissions
        assertEquals(3, permissions.size)
    }
}
