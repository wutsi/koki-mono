package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
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
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchFileResponse {
        val files = service.search(
            tenantId = tenantId,
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset
        )
        val labels = service.getLabels(files)
        return SearchFileResponse(
            files = files.map { file -> mapper.toFileSummary(file, labels) }
        )
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        service.delete(id, tenantId)
        publisher.publish(
            FileDeletedEvent(
                fileId = id,
                tenantId = tenantId,
            )
        )
    }

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam(name = "tenant-id") tenantId: Long,
        @RequestParam(required = false, name = "access-token") accessToken: String? = null,
        @RequestParam(required = false, name = "owner-id") ownerId: Long? = null,
        @RequestParam(required = false, name = "owner-type") ownerType: ObjectType? = null,
        @RequestPart file: MultipartFile,
    ): UploadFileResponse {
        response.addHeader("Access-Control-Allow-Origin", "*")

        val file = service.upload(
            tenantId = tenantId,
            file = file,
            userId = accessToken?.let { toUserId(accessToken) },
            ownerId = ownerId,
            ownerType = ownerType,
        )

        publisher.publish(
            FileUploadedEvent(
                fileId = file.id!!,
                tenantId = tenantId,
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

    private fun toUserId(accessToken: String): Long? {
        val principal = JWTDecoder().decode(accessToken)
        return principal.getUserId()
    }
}
