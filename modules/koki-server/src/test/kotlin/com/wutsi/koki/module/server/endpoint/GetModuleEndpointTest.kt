package com.wutsi.koki.module.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.module.dto.GetModuleResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/module/GetModuleEndpoint.sql"])
class GetModuleEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/modules/100", GetModuleResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val module = response.body!!.module
        assertEquals("MODULE1", module.name)
        assertEquals("Module 1", module.title)
        assertEquals("This is a module", module.description)
        assertEquals("/module1", module.homeUrl)
        assertEquals("/module1/tab", module.tabUrl)
        assertEquals("/settings/module1", module.settingsUrl)
    }
}
