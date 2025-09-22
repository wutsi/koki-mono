package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.BadRequestException
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.file.dto.SearchFileResponse
import com.wutsi.koki.file.dto.UploadFileResponse
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.mapper.FileMapper
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.security.dto.JWTDecoder
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.hssf.usermodel.HeaderFooter.file
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/v1/files")
class FileEndpoints(
    private val service: FileService,
    private val mapper: FileMapper,
    private val response: HttpServletResponse,
    private val publisher: Publisher,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetFileResponse {
        val file = service.get(id, tenantId)
        return GetFileResponse(
            file = mapper.toFile(file)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false, name = "type") type: FileType? = null,
        @RequestParam(required = false) status: FileStatus? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchFileResponse {
        val files = service.search(
            tenantId = tenantId,
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            type = type,
            status = status,
            limit = limit,
            offset = offset
        )

        return SearchFileResponse(
            files = files.map { file -> mapper.toFileSummary(file) }
        )
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        val file = service.delete(id, tenantId)
        publisher.publish(
            FileDeletedEvent(
                fileId = id,
                tenantId = tenantId,
                fileType = file.type,
                owner = if (file.ownerId != null && file.ownerType != null) {
                    ObjectReference(file.ownerId, file.ownerType)
                } else {
                    null
                }
            )
        )
    }

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam(name = "tenant-id") tenantId: Long,
        @RequestParam(required = false, name = "access-token") accessToken: String? = null,
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(name = "type") type: FileType? = null,
        @RequestPart file: MultipartFile,
    ): UploadFileResponse {
        validate(file, type)

        response.addHeader("Access-Control-Allow-Origin", "*")

        val file = service.upload(
            tenantId = tenantId,
            file = file,
            userId = accessToken?.let { toUserId(accessToken) },
            type = type,
            ownerId = ownerId,
            ownerType = ownerType,
        )

        publisher.publish(
            FileUploadedEvent(
                fileId = file.id!!,
                tenantId = tenantId,
                fileType = file.type,
                owner = ownerId?.let { id ->
                    ownerType?.let { type -> ObjectReference(id, type) }
                }
            )
        )

        return UploadFileResponse(
            id = file.id,
            name = file.name,
        )
    }

    private fun validate(file: MultipartFile, type: FileType?) {
        if (type == FileType.IMAGE && file.contentType?.startsWith("image/") == false) {
            throw BadRequestException(
                error = Error(
                    code = ErrorCode.FILE_NOT_IMAGE,
                    data = file.contentType?.let { contentType ->
                        mapOf("contentType" to contentType)
                    }
                )
            )
        }
    }

    private fun toUserId(accessToken: String): Long? {
        val principal = JWTDecoder().decode(accessToken)
        return principal.getUserId()
    }
}
