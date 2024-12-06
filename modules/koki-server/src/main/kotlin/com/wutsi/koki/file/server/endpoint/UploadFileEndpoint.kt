package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.file.dto.UploadFileResponse
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.security.dto.JWTDecoder
import jakarta.servlet.http.HttpServletResponse
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
class UploadFileEndpoint(
    private val service: FileService,
    private val response: HttpServletResponse,
) {
    private val jwtDecoder = JWTDecoder()

    @ResponseBody
    @PostMapping("/v1/files/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "form-id") formId: String? = null,
        @RequestParam(name = "tenant-id") tenantId: Long,
        @RequestParam(required = false, name = "access-token") accessToken: String? = null,
        @RequestPart file: MultipartFile,
    ): UploadFileResponse {
        response.addHeader("Access-Control-Allow-Origin", "*")

        val file = service.upload(
            workflowInstanceId = workflowInstanceId,
            formId = formId,
            tenantId = tenantId,
            file = file,
            userId = accessToken?.let { toUserId(accessToken) }
        )
        return UploadFileResponse(
            id = file.id!!,
            name = file.name,
        )
    }

    private fun toUserId(accessToken: String): Long? {
        val principal = jwtDecoder.decode(accessToken)
        return principal.getUserId()
    }
}
