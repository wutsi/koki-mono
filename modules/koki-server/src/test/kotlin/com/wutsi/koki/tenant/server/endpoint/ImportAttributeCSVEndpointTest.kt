package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.tenant.dto.AttributeType
import com.wutsi.koki.tenant.server.dao.AttributeRepository
import com.wutsi.koki.tenant.server.domain.AttributeEntity
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

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/ImportCSVAttributeEndpoint.sql"])
class ImportCSVAttributeEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AttributeRepository

    override fun getTenantId() = 1L

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
            "/v1/attributes/csv",
            HttpMethod.POST,
            requestEntity,
            ImportResponse::class.java,
        ).body!!
    }

    @Test
    fun import() {
        val response = upload(
            """
                "name","type","active","choices","label","description"
                "a","DECIMAL","Yes",,,
                "b","TEXT","No","P1|P2|P3|P4","Priority","Priority of the ticket"
                "c","FILE",,,,
                "new","image","yes","","",""
            """.trimIndent()
        )

        assertEquals(3, response.updated)
        assertEquals(1, response.added)
        assertEquals(0, response.errors)
        assertTrue(response.errorMessages.isEmpty())

        val attrA = findAttribute("a")
        assertEquals("a", attrA.name)
        assertEquals(AttributeType.DECIMAL, attrA.type)
        assertTrue(attrA.active)
        assertNull(attrA.choices)
        assertNull(attrA.label)
        assertNull(attrA.description)

        val attrB = findAttribute("b")
        assertEquals("b", attrB.name)
        assertEquals(AttributeType.TEXT, attrB.type)
        assertFalse(attrB.active)
        assertEquals("P1\nP2\nP3\nP4", attrB.choices)
        assertEquals("Priority", attrB.label)
        assertEquals("Priority of the ticket", attrB.description)

        val attrC = findAttribute("c")
        assertEquals("c", attrC.name)
        assertEquals(AttributeType.FILE, attrC.type)
        assertFalse(attrC.active)
        assertNull(attrC.choices)
        assertNull(attrC.label)
        assertNull(attrC.description)

        val attrNew = findAttribute("new")
        assertEquals("new", attrNew.name)
        assertEquals(AttributeType.IMAGE, attrNew.type)
        assertTrue(attrNew.active)
        assertNull(attrNew.choices)
        assertNull(attrNew.label)
        assertNull(attrNew.description)
    }

    @Test
    fun noName() {
        val response = upload(
            """
                "name","type","active","choices","label","description"
                "","TEXT","No","P1|P2|P3|P4","Priority","Priority of the ticket"
            """.trimIndent()
        )

        assertEquals(0, response.updated)
        assertEquals(0, response.added)
        assertEquals(1, response.errors)
        assertFalse(response.errorMessages.isEmpty())
        assertEquals(ErrorCode.ATTRIBUTE_NAME_MISSING, response.errorMessages[0].code)
    }

    @Test
    fun noType() {
        checkTypeInvalid("")
    }

    @Test
    fun invalidType() {
        checkTypeInvalid("\"xx\"")
    }

    @Test
    fun unknownType() {
        checkTypeInvalid("\"UNKNOWN\"")
    }

    private fun checkTypeInvalid(type: String) {
        val response = upload(
            """
                "name","type","active","choices","label","description"
                "a",$type,"No","P1|P2|P3|P4","Priority","Priority of the ticket"
            """.trimIndent()
        )

        assertEquals(0, response.updated)
        assertEquals(0, response.added)
        assertEquals(1, response.errors)
        assertFalse(response.errorMessages.isEmpty())
        assertEquals(ErrorCode.ATTRIBUTE_TYPE_INVALID, response.errorMessages[0].code)
    }

    private fun findAttribute(name: String): AttributeEntity {
        return dao.findByTenantIdAndNameIn(getTenantId(), listOf(name)).first()
    }
}
