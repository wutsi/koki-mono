package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.workflow.dto.SearchActivityResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/SearchActivityEndpoint.sql"])
class SearchActivityEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/activities", SearchActivityResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.activities
        assertEquals(10, workflows.size)
    }

    @Test
    fun type() {
        val result =
            rest.getForEntity("/v1/activities?type=START", SearchActivityResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val activities = result.body!!.activities
        assertEquals(2, activities.size)

        assertEquals(100L, activities[0].id)
        assertEquals(110L, activities[1].id)
    }

    @Test
    fun ids() {
        val result = rest.getForEntity(
            "/v1/activities?id=100&id=101&id=110&limit=10&offset=0",
            SearchActivityResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activities = result.body!!.activities
        assertEquals(3, activities.size)
    }

    @Test
    fun `exclude workflow from other tenant`() {
        val result = rest.getForEntity(
            "/v1/activities?id=100&id=101&id=200",
            SearchActivityResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activities = result.body!!.activities
        assertEquals(2, activities.size)
    }
}
