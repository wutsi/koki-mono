package com.wutsi.koki.portal.pub.home.page

import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.TenantFixtures
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import kotlin.test.Test
import kotlin.test.assertEquals

class ManifestControllerTest : AbstractPageControllerTest() {
    @Test
    fun manifest() {
        val response = RestTemplate().getForEntity("http://localhost:$port/manifest.json", Map::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val manifest = response.body as Map<*, *>
        assertEquals(TenantFixtures.tenants[0].name, manifest["name"])
        assertEquals(TenantFixtures.tenants[0].name, manifest["shortName"])
        assertEquals("standalone", manifest["display"])
        assertEquals("/", manifest["scope"])
        assertEquals("any", manifest["orientation"])
    }
}
