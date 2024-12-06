package com.wutsi.koki.portal.page.file

import com.wutsi.koki.portal.service.FileService
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URL

@RestController
@RequestMapping
class FileController(
    private val fileService: FileService,
    private val response: HttpServletResponse,
) {
    @GetMapping("/files/{id}/{filename}")
    fun download(
        @PathVariable id: String,
        @PathVariable filename: String,
    ) {
        val file = fileService.file(id)
        response.contentType = file.contentType
        IOUtils.copy(URL(file.contentUrl).openStream(), response.outputStream)
    }
}
