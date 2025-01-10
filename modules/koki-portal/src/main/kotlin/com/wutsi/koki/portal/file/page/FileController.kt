package com.wutsi.koki.portal.file.page

import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.net.URL
import java.net.URLEncoder

@Controller
@RequestMapping
class FileController(
    private val service: FileService,
    private val response: HttpServletResponse,
) : AbstractPageController() {
    @GetMapping("/files/{id}")
    fun show(
        @PathVariable id: Long,
        @RequestParam(required = false, name = "return-url") returnUrl: String? = null,
        model: Model,
    ): String {
        val file = service.file(id)
        return show(file, returnUrl, model)
    }

    private fun show(file: FileModel, returnUrl: String?, model: Model): String {
        model.addAttribute("file", file)
        model.addAttribute(
            "deleteUrl",
            "/files/${file.id}/delete" +
                (returnUrl?.let { "?return-url=" + URLEncoder.encode(returnUrl, "utf-8") } ?: "")
        )
        model.addAttribute("returnUrl", returnUrl)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.FILE,
                title = file.name
            )
        )
        return "files/show"
    }

    @GetMapping("/files/{id}/delete")
    fun delete(
        @PathVariable id: Long,
        @RequestParam(required = false, name = "return-url") returnUrl: String? = null,
        model: Model
    ): String {
        val file = service.file(id)
        try {
            service.delete(id)

            model.addAttribute("file", file)
            model.addAttribute("returnUrl", returnUrl)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.FILE_DELETED,
                    title = file.name,
                )
            )
            return "files/deleted"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(file, returnUrl, model)
        }
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
