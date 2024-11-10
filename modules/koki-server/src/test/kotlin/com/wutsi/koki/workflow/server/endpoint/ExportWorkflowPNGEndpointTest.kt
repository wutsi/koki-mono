package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ExportWorkflowPNGEndpoint.sql"])
class ExportWorkflowPNGEndpointTest : TenantAwareEndpointTest() {
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
}
