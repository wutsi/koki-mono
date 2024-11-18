package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ExportWorkflowJSONEndpoint.sql"])
class ExportWorkflowJSONEndpointTest : TenantAwareEndpointTest() {
    @LocalServerPort
    private lateinit var port: Integer

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
        ImageIO.read(file)
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
