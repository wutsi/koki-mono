package com.wutsi.koki.workflow.server.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.ImportWorkflowRequest
import com.wutsi.koki.workflow.dto.WorkflowData
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ExportWorkflowPNGEndpoint.sql"])
class ExportWorkflowPNGEndpointTest : TenantAwareEndpointTest() {
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
            expectedContentType = "image/png",
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
    fun png() {
        val url = "http://localhost:$port/v1/workflows/images/1.100.png"
        val file = download(url, 200)
        assertTrue(file!!.length() > 0L)
        assertTrue(file.length() > 0)
        ImageIO.read(file)
    }

    @Test
    fun empty() {
        val url = "http://localhost:$port/v1/workflows/images/1.110.png"
        val file = download(url, 200)
        assertEquals(0L, file!!.length())
    }

    @Test
    fun `not found`() {
        val url = "http://localhost:$port/v1/workflows/images/1.9999.png"
        download(url, 404)
    }

    @Test
    fun `workflow of another tenant`() {
        val url = "http://localhost:$port/v1/workflows/images/1.200.png"
        download(url, 404)
    }

    @Test
    fun post() {
        val request = ImportWorkflowRequest(
            workflow = WorkflowData(
                name = "new",
                description = "This is a new workflow",
                parameters = listOf("PARAM_1 ", "PARAM_2"),
                approverRole = "accountant",
                activities = listOf(
                    ActivityData(name = "START", type = ActivityType.START),
                    ActivityData(
                        name = "INVOICE",
                        title = "Invoicing...",
                        description = "SAGE create an invoice",
                        type = ActivityType.MANUAL,
                        input = mapOf("foo" to "bar", "a" to "b"),
                        output = mapOf("x" to "y"),
                        requiresApproval = true,
                        role = "accountant",
                        form = "f-100",
                        message = "m-100",
                        script = "s-100",
                    ),
                    ActivityData(
                        name = "STOP",
                        type = ActivityType.END,
                    ),
                ),
                flows = listOf(
                    FlowData(from = "START", to = "INVOICE"),
                    FlowData(from = "INVOICE", to = "STOP", expression = "A==true"),
                )
            )
        )

        val json = objectMapper.writeValueAsString(request)
        val url = URL("http://localhost:$port/v1/workflows/images")
        val cnn = url.openConnection() as HttpURLConnection
        try {
            cnn.requestMethod = "POST"
            cnn.setRequestProperty("Content-Type", "application/json")
            cnn.doOutput = true
            val os = cnn.outputStream
            IOUtils.copy(ByteArrayInputStream(json.toByteArray()), os)

            cnn.connect()

            assertEquals(200, cnn.responseCode)
            assertEquals("image/png", cnn.contentType)
        } finally {
            cnn.disconnect()
        }
    }

    @Test
    fun postWithError() {
        val request = ImportWorkflowRequest(
            workflow = WorkflowData(
                name = "new",
                description = "This is a new workflow",
                parameters = listOf("PARAM_1 ", "PARAM_2"),
                approverRole = "accountant",
                activities = listOf(),
                flows = listOf()
            )
        )

        val json = objectMapper.writeValueAsString(request)
        val url = URL("http://localhost:$port/v1/workflows/images")
        val cnn = url.openConnection() as HttpURLConnection
        try {
            cnn.requestMethod = "POST"
            cnn.setRequestProperty("Content-Type", "application/json")
            cnn.doOutput = true
            val os = cnn.outputStream
            IOUtils.copy(ByteArrayInputStream(json.toByteArray()), os)

            cnn.connect()

            assertEquals(400, cnn.responseCode)
            assertEquals("application/json", cnn.contentType)
        } finally {
            cnn.disconnect()
        }
    }
}
