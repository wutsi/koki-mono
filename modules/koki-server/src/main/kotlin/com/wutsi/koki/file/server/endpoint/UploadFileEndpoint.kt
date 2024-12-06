package com.wutsi.koki.portal.page.file

import com.wutsi.koki.file.dto.UploadFileResponse
import com.wutsi.koki.file.server.service.FileService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping
class UploadFileController(
    private val service: FileService,
) {
    @ResponseBody
    @PostMapping("/v1/files/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String?,
        @RequestParam(required = false, name = "form-id") formId: String?,
        @RequestParam(name = "tenant-id") tenantId: Long,
        @RequestPart file: MultipartFile
    ): UploadFileResponse {
        val file = service.upload(
            workflowInstanceId = workflowInstanceId,
            formId = formId,
            tenantId = tenantId,
            file = file
        )
        return UploadFileResponse(fileId = file.id!!)
    }
}
