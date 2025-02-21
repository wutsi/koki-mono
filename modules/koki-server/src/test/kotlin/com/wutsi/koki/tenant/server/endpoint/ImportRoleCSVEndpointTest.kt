package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.tenant.server.dao.RoleRepository
import com.wutsi.koki.tenant.server.domain.RoleEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/ImportRoleCSVEndpoint.sql"])
class ImportRoleCSVEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: RoleRepository

    private fun upload(body: String): ImportResponse {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA

        val fileMap = LinkedMultiValueMap<String, String>()
        val contentDisposition = ContentDisposition
            .builder("form-data")
            .name("file")
            .filename("test.csv")
            .build()
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        val fileEntity = HttpEntity(body.toByteArray(), fileMap)

        val body = LinkedMultiValueMap<String, Any>()
        body.add("file", fileEntity)

        val requestEntity = HttpEntity<MultiValueMap<String, Any>>(body, headers)
        return rest.exchange(
            "/v1/roles/csv",
            HttpMethod.POST,
            requestEntity,
            ImportResponse::class.java,
        ).body!!
    }

    @Test
    fun import() {
        val response = upload(
            """
                "name","title","active","description"
                "a","RoleA","Yes",,,
                "b","RoleB","No","Priority of the ticket"
                "c",,,
                "new",,"yes",""
            """.trimIndent()
        )

        assertEquals(4, response.updated)
        assertEquals(1, response.added)
        assertEquals(0, response.errors)
        assertTrue(response.errorMessages.isEmpty())

        val roleA = findRole("a")
        assertEquals("a", roleA.name)
        assertEquals("RoleA", roleA.title)
        assertEquals(TENANT_ID, roleA.tenantId)
        assertTrue(roleA.active)
        assertNull(roleA.description)

        val roleB = findRole("b")
        assertEquals("b", roleB.name)
        assertEquals("RoleB", roleB.title)
        assertEquals(TENANT_ID, roleB.tenantId)
        assertFalse(roleB.active)
        assertEquals("Priority of the ticket", roleB.description)

        val roleC = findRole("c")
        assertEquals(TENANT_ID, roleC.tenantId)
        assertEquals("c", roleC.name)
        assertNull(roleC.title)
        assertFalse(roleC.active)
        assertNull(roleC.description)

        val roleX = findRole("x")
        assertFalse(roleX.active)

        val roleNew = findRole("new")
        assertEquals(TENANT_ID, roleNew.tenantId)
        assertEquals("new", roleNew.name)
        assertNull(roleNew.title)
        assertTrue(roleNew.active)
        assertNull(roleNew.description)
    }

    @Test
    fun noName() {
        val response = upload(
            """
                "name","active","description"
                "","No","Priority of the ticket"
            """.trimIndent()
        )

        assertEquals(1, response.errors)
        assertFalse(response.errorMessages.isEmpty())
        assertEquals(ErrorCode.ROLE_NAME_MISSING, response.errorMessages[0].code)
    }

    @Test
    fun `malformed row`() {
        val response = upload(
            """
                "name","active","description"
                "b11"
            """.trimIndent()
        )

        assertEquals(1, response.errors)

        assertFalse(response.errorMessages.isEmpty())
        assertEquals(ErrorCode.IMPORT_ERROR, response.errorMessages[0].code)
    }

    private fun findRole(name: String): RoleEntity {
        return dao.findByTenantIdAndNameIn(TENANT_ID, listOf(name)).first()
    }
}
