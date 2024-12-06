package com.wutsi.koki.platform.storage.local

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaTypeFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.jvm.optionals.getOrNull

class LocalStorageServlet(private val directory: String) : HttpServlet() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LocalStorageServlet::class.java)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val file = File("$directory${req.pathInfo}")
        try {
            output(file, resp)
        } catch (e: FileNotFoundException) {
            resp.sendError(404)
            LOGGER.error("File not found: $file", e)
        } catch (e: Exception) {
            resp.sendError(500)
            LOGGER.error("Unexpected error while processing file $file", e)
        }
    }

    private fun output(file: File, resp: HttpServletResponse) {
        resp.contentType = probeContentType(file)

        FileInputStream(file).use { input ->
            val contentLength = input.copyTo(resp.outputStream)
            resp.setContentLength(contentLength.toInt())
        }
    }

    private fun probeContentType(file: File): String {
        return MediaTypeFactory.getMediaType(file.name)
            .getOrNull()
            ?.toString()
            ?: "application/octet-stream"
    }
}
