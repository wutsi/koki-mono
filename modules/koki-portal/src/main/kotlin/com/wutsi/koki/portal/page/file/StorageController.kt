package com.wutsi.koki.portal.page.file

import com.wutsi.koki.portal.model.FileModel
import com.wutsi.koki.portal.service.FileService
import com.wutsi.koki.portal.service.storage.StorageService
import com.wutsi.koki.sdk.TenantProvider
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.net.URLEncoder

@RestController
@RequestMapping
class StorageController(
    private val storage: StorageService,
    private val fileService: FileService,
    private val tenantProvider: TenantProvider,
    private val response: HttpServletResponse,
) {
    @ResponseBody
    @PostMapping("/storage", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String?,
        @RequestParam(required = false, name = "form-id") formId: String?,
        @RequestPart file: MultipartFile
    ): FileModel {
        val path = toPath(file, formId, workflowInstanceId)
        val url = storage.store(
            path = path.toString(),
            content = file.inputStream,
            contentType = file.contentType,
            contentLength = file.size
        )

        val fileId = fileService.create(
            file = file,
            url = url,
            workflowInstanceId = workflowInstanceId,
            formId = formId,
        )

        return FileModel(
            id = fileId,
            name = file.originalFilename ?: "",
            formId = formId,
            workflowInstanceId = workflowInstanceId,
        )
    }

    @GetMapping("/storage/{id}/{filename}")
    fun download(
        @PathVariable id: String,
        @PathVariable filename: String,
    ) {
        val file = fileService.file(id)
        response.contentType = file.contentType
        storage.get(URL(file.contentUrl), response.outputStream)
    }

    private fun toPath(file: MultipartFile, formId: String?, workflowInstanceId: String?): String {
        val path = StringBuilder("tenant/${tenantProvider.id()}")
        if (formId != null) {
            path.append("/form/$formId")
        }
        if (workflowInstanceId != null) {
            path.append("/workflow-instance/$workflowInstanceId")
        }
        path.append("/" + URLEncoder.encode(file.originalFilename, "utf-8"))
        return path.toString()
    }
}
