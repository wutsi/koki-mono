package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
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

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ExportWorkflowInstancePNGEndpoint.sql"])
class ExportWorkflowInstancePNGEndpointTest : TenantAwareEndpointTest() {
    @LocalServerPort
    private lateinit var port: Integer

    private val folder = File(File(System.getProperty("user.home")), "__wutsi")

    private fun download(u: String, statusCode: Int): File? {
        val url = URL(u)
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
