package com.wutsi.koki.portal.file.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.security.RequiresPermission
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.io.ByteArrayOutputStream
import java.net.URL

@Controller
@RequestMapping
@RequiresPermission(["file"])
class FileController(
    private val service: FileService,
    private val response: HttpServletResponse,
) : AbstractPageController() {
    @GetMapping("/files/{id}/delete")
    @RequiresPermission(["file:delete"])
    fun delete(@PathVariable id: Long): String {
        service.delete(id)
        return "files/deleted"
    }

    @GetMapping("/files/{id}/download")
    fun download(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val file = service.file(id)

        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(file.contentType)
        headers.contentLength = file.contentLength
        headers.setContentDispositionFormData(file.name, file.name)

        val output = ByteArrayOutputStream()
        URL(file.contentUrl).openStream().use { input ->
            IOUtils.copy(input, output)
        }

        return ResponseEntity(output.toByteArray(), headers, HttpStatus.OK)
    }
}
