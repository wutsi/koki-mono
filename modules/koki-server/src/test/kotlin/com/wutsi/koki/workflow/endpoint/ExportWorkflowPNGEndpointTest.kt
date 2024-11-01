package com.wutsi.koki.tenant.server.endpoint

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ExportWorkflowPNGEndpoint.sql"])
class ExportWorkflowPNGEndpointTest : TenantAwareEndpointTest() {
    @LocalServerPort
    private lateinit var port: Integer

    private val folder = File(File(System.getProperty("user.home")), "__wutsi")

    private fun download(url: URL, filename: String, statusCode: Int): File? {
        val cnn = url.openConnection() as HttpURLConnection
        try {
            cnn.connect()

            val i = url.file.lastIndexOf("/")
            val filename = url.file.substring(i + 1)

            assertEquals("image/png", cnn.contentType)
            assertEquals("attachment; filename=\"$filename\"", cnn.getHeaderField("Content-Disposition"))
            assertEquals(statusCode, cnn.responseCode)

            if (statusCode == 200) {
                val file = File(folder, filename)
                val output = FileOutputStream(file)
                output.use {
                    IOUtils.copy(cnn.inputStream, output)
                }
                return file
            } else {
                return null
            }
        } finally {
            cnn.disconnect()
        }
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
        val url = URL("http://localhost:$port/v1/workflows/image/1-100.png")
        val file = download(url, "workflow-1-100.png", 200)
        assertTrue(file!!.length() > 0L)
        ImageIO.read(file)
    }

    @Test
    fun empty() {
        val url = URL("http://localhost:$port/v1/workflows/image/1-110.png")
        val file = download(url, "workflow-1-110.png", 200)
        assertEquals(0L, file!!.length())
    }

    @Test
    fun `not found`() {
        val url = URL("http://localhost:$port/v1/workflows/image/1-9999.png")
        download(url, "workflow-1-9999.png", 404)
    }

    @Test
    fun `workflow of another tenant`() {
        val url = URL("http://localhost:$port/v1/workflows/image/1-200.png")
        download(url, "workflow-1-200.png.png", 404)
    }
}
