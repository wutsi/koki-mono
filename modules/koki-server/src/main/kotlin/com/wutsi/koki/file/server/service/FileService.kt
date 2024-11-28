package com.wutsi.koki.file.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.server.dao.FileRepository
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@Service
class FileService(
    private val dao: FileRepository,
    private val securityService: SecurityService,
    private val em: EntityManager,
) {
    fun get(id: String, tenantId: Long): FileEntity {
        val file = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.FILE_NOT_FOUND)) }

        if (file.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.FILE_NOT_FOUND))
        }
        return file
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        workflowInstanceIds: List<String> = emptyList(),
        formIds: List<String> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<FileEntity> {
        val jql = StringBuilder("SELECT F FROM FileEntity AS F WHERE F.tenantId=:tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND F.id IN :ids")
        }
        if (workflowInstanceIds.isNotEmpty()) {
            jql.append(" AND F.workflowInstanceId IN :workflowInstanceIds")
        }
        if (formIds.isNotEmpty()) {
            jql.append(" AND F.formId IN :formIds")
        }
        jql.append(" ORDER BY LOWER(F.name)")

        val query = em.createQuery(jql.toString(), FileEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (workflowInstanceIds.isNotEmpty()) {
            query.setParameter("workflowInstanceIds", workflowInstanceIds)
        }
        if (formIds.isNotEmpty()) {
            query.setParameter("formIds", formIds)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun create(request: CreateFileRequest, tenantId: Long): FileEntity {
        val now = Date()
        return dao.save(
            FileEntity(
                id = UUID.randomUUID().toString(),
                tenantId = tenantId,
                createdById = securityService.getCurrentUserIdOrNull(),
                name = request.name,
                url = request.url,
                workflowInstanceId = request.workflowInstanceId,
                formId = request.formId,
                contentType = request.contentType,
                contentLength = request.contentLength,
                createdAt = now,
                modifiedAt = now,
            )
        )
    }
}
