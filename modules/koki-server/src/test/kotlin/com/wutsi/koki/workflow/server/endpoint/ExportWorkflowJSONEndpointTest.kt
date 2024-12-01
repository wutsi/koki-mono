package com.wutsi.koki.tenant.server.server.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ExportWorkflowJSONEndpoint.sql"])
class ExportWorkflowJSONEndpointTest : TenantAwareEndpointTest() {
    @LocalServerPort
    private lateinit var port: Integer

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val folder = File(File(System.getProperty("user.home")), "__wutsi")

    private fun download(url: String, statusCode: Int): File? {
        val i = url.lastIndexOf("/")
        val filename = url.substring(i + 1)

        return super.download(
            url,
            expectedFileName = filename,
            expectedStatusCode = statusCode,
            expectedContentType = "application/json",
            accessToken = null
        )
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()

        if (!folder.exists()) {
            folder.mkdirs()
        }
    }

    @Test
    fun json() {
        val url = "http://localhost:$port/v1/workflows/json/1.100.json"
        val file = download(url, 200)
        assertTrue(file!!.length() > 0L)
        assertTrue(file.length() > 0)

        val json = file.readText()
        val workflow = objectMapper.readValue(json, WorkflowData::class.java)
        assertEquals("w100", workflow.name)
        assertEquals("workflow #100", workflow.title)
        assertEquals("Yo", workflow.description)
        assertEquals(listOf("PARAM_1", "PARAM_2"), workflow.parameters)
        assertEquals("admin", workflow.approverRole)
        assertEquals(3, workflow.activities.size)

        val activities = workflow.activities.sortedBy { it.name }
        assertEquals("START", activities[0].name)
        assertEquals("Start", activities[0].title)
        assertEquals(ActivityType.START, activities[0].type)
        assertEquals(null, activities[0].form)
        assertEquals(null, activities[0].role)
        assertEquals(null, activities[0].message)
        assertEquals(false, activities[0].requiresApproval)
        assertEquals("Starting the process", activities[0].description)

        assertEquals("STOP", activities[1].name)
        assertEquals("Done", activities[1].title)
        assertEquals(ActivityType.END, activities[1].type)
        assertEquals(null, activities[1].form)
        assertEquals(null, activities[1].role)
        assertEquals(null, activities[1].message)
        assertEquals(false, activities[1].requiresApproval)
        assertEquals(null, activities[1].description)

        assertEquals("WORKING", activities[2].name)
        assertEquals("Work...", activities[2].title)
        assertEquals(ActivityType.MANUAL, activities[2].type)
        assertEquals("f-100", activities[2].form)
        assertEquals("admin", activities[2].role)
        assertEquals("M-100", activities[2].message)
        assertEquals(true, activities[2].requiresApproval)
        assertEquals("Performing the task", activities[2].description)

        assertEquals(2, workflow.flows.size)
        assertEquals("START", workflow.flows[0].from)
        assertEquals("WORKING", workflow.flows[0].to)
        assertEquals(null, workflow.flows[0].expression)

        assertEquals("WORKING", workflow.flows[1].from)
        assertEquals("STOP", workflow.flows[1].to)
        assertEquals("submit=true", workflow.flows[1].expression)
    }

    @Test
    fun `not found`() {
        val url = "http://localhost:$port/v1/workflows/json/1.9999.json"
        download(url, 404)
    }

    @Test
    fun `workflow of another tenant`() {
        val url = "http://localhost:$port/v1/workflows/json/1.200.json"
        download(url, 404)
    }
}
