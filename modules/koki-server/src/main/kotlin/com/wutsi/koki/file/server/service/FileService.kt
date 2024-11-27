package com.wutsi.koki.document.server.service

import com.wutsi.koki.document.dto.CreateFileRequest
import com.wutsi.koki.document.server.dao.FileRepository
import com.wutsi.koki.document.server.domain.FileEntity
import com.wutsi.koki.security.server.service.SecurityService
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class FileService(
    private val dao: FileRepository,
    private val securityService: SecurityService,
) {
    fun create(request: CreateFileRequest, tenantId: Long): FileEntity {
        val now = Date()
        return dao.save(
            FileEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                createById = securityService.getCurrentUserIdOrNull(),
                name = request.name,
                url = request.url,
                workflowInstanceId = request.workflowInstanceId,
                contentType = request.contentType,
                contentLength = request.contentLength,
                createdAt = now,
                modifiedAt = now,
            )
        )
    }
}
