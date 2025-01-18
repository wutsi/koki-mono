package com.wutsi.koki.module.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.module.dto.SearchModuleResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/module/SearchModuleEndpoint.sql"])
class SearchModuleEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/modules", SearchModuleResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val modules = response.body!!.modules
        assertEquals(3, modules.size)
    }
}
