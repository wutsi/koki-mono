package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ExportWorkflowInstancePNGEndpoint.sql"])
class ExportWorkflowInstancePNGEndpointTest : TenantAwareEndpointTest() {
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
            expectedContentType = "image/png"
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
        val url = "http://localhost:$port/v1/workflow-instances/images/1.wi-100-01.png"
        val file = download(url, 200)
        assertTrue(file!!.length() > 0L)
        assertTrue(file.length() > 0)
        ImageIO.read(file)
    }

    @Test
    fun `not found`() {
        val url = "http://localhost:$port/v1/workflow-instances/images/1.xfdlklkx.png"
        download(url, 404)
    }
}
