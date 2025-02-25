package com.wutsi.koki.portal.file.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.security.RequiresPermission
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
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
    fun download(@PathVariable id: Long) {
        val file = service.file(id)
        response.contentType = file.contentType
        response.setContentLength(file.contentLength.toInt())
        response.setHeader("Content-Disposition", "attachment; filename=\"${file.name}\"")
        URL(file.contentUrl)
            .openStream()
            .use { inputStream ->
                IOUtils.copy(inputStream, response.outputStream)
            }
    }
}
