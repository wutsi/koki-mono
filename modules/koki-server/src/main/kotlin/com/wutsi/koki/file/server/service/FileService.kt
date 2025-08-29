package com.wutsi.koki.file.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.dao.FileRepository
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import jakarta.persistence.EntityManager
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.util.Date
import java.util.UUID

@Service
class FileService(
    private val dao: FileRepository,
    private val storageBuilder: StorageServiceBuilder,
    private val configurationService: ConfigurationService,
    private val securityService: SecurityService,
    private val storageProvider: StorageProvider,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): FileEntity {
        val file = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.FILE_NOT_FOUND)) }

        if (file.tenantId != tenantId || file.deleted) {
            throw NotFoundException(Error(ErrorCode.FILE_NOT_FOUND))
        }
        return file
    }

    fun countByTypeAndOwnerIdAndOwnerType(type: FileType, ownerId: Long, ownerType: ObjectType): Long? {
        return dao.countByTypeAndOwnerIdAndOwnerType(type, ownerId, ownerType)
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        type: FileType? = null,
        status: FileStatus? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<FileEntity> {
        if (limit == 0) {
            return emptyList()
        }

        val jql = StringBuilder("SELECT F FROM FileEntity AS F")
        jql.append(" WHERE F.deleted=false AND F.tenantId=:tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND F.id IN :ids")
        }
        if (ownerId != null) {
            jql.append(" AND F.ownerId = :ownerId")
        }
        if (ownerType != null) {
            jql.append(" AND F.ownerType = :ownerType")
        }
        if (type != null) {
            jql.append(" AND F.type = :type")
        }
        if (status != null) {
            jql.append(" AND F.status = :status")
        }
        jql.append(" ORDER BY F.id DESC")

        val query = em.createQuery(jql.toString(), FileEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (ownerId != null) {
            query.setParameter("ownerId", ownerId)
        }
        if (ownerType != null) {
            query.setParameter("ownerType", ownerType)
        }
        if (type != null) {
            query.setParameter("type", type)
        }
        if (status != null) {
            query.setParameter("status", status)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun upload(
        userId: Long?,
        file: MultipartFile,
        type: FileType?,
        ownerId: Long?,
        ownerType: ObjectType?,
        tenantId: Long,
    ): FileEntity {
        // Store
        val url = store(
            filename = file.originalFilename ?: file.name,
            content = file.inputStream,
            contentType = file.contentType,
            contentLength = file.size,
            ownerId = ownerId,
            ownerType = ownerType,
            tenantId = tenantId,
        )

        // Create the file
        return create(
            filename = file.originalFilename ?: file.name,
            contentType = file.contentType,
            contentLength = file.size,
            userId = userId,
            url = url,
            ownerId = ownerId,
            ownerType = ownerType,
            tenantId = tenantId,
            type = type,
        )
    }

    @Transactional
    fun create(
        filename: String,
        contentType: String?,
        contentLength: Long,
        userId: Long?,
        url: URL,
        ownerId: Long?,
        ownerType: ObjectType?,
        type: FileType?,
        tenantId: Long,
    ): FileEntity {
        return dao.save(
            FileEntity(
                createdById = userId ?: securityService.getCurrentUserIdOrNull(),
                tenantId = tenantId,
                name = filename,
                url = url.toString(),
                contentType = contentType ?: "application/octet-stream",
                contentLength = contentLength,
                type = type ?: FileType.FILE,
                ownerId = ownerId,
                ownerType = ownerType,
                status = FileStatus.UNDER_REVIEW,
            )
        )
    }

    @Transactional
    fun store(
        filename: String,
        contentType: String?,
        contentLength: Long,
        content: InputStream,
        ownerId: Long?,
        ownerType: ObjectType?,
        tenantId: Long,
    ): URL {
        // Store
        val fileId = UUID.randomUUID().toString()
        val path = if (ownerId != null && ownerType != null) {
            toPath(filename, ownerId, ownerType, fileId, tenantId)
        } else {
            toPath(filename, fileId, tenantId)
        }
        return getStorageService(tenantId).store(
            path = path.toString(),
            content = content,
            contentType = contentType,
            contentLength = contentLength,
        )
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val file = get(id, tenantId)
        file.deleted = true
        file.deletedAt = Date()
        file.deletedById = securityService.getCurrentUserIdOrNull()
        dao.save(file)
    }

    @Transactional
    fun save(file: FileEntity): FileEntity {
        file.modifiedAt = Date()
        return dao.save(file)
    }

    fun download(file: FileEntity): File {
        val extension = FilenameUtils.getExtension(file.url)
        val f = File.createTempFile("file-${file.id}", ".$extension")
        val output = FileOutputStream(f)
        output.use {
            storageProvider.get(file.tenantId).get(URI(file.url).toURL(), output)
        }
        return f
    }

    private fun toPath(
        filename: String,
        fileId: String,
        tenantId: Long,
    ): String {
        val path = StringBuilder("tenant/$tenantId/uploads")
        path.append("/$fileId")
        path.append("/$filename")
        return path.toString()
    }

    private fun toPath(
        filename: String,
        ownerId: Long,
        ownerType: ObjectType,
        fileId: String,
        tenantId: Long,
    ): String {
        val path = StringBuilder("tenant/$tenantId")
        path.append("/").append(ownerType.name.lowercase())
        path.append("/").append(ownerId)
        path.append("/$fileId")
        path.append("/$filename")
        return path.toString()
    }

    private fun getStorageService(tenantId: Long): StorageService {
        val configs = configurationService.search(
            tenantId = tenantId, keyword = "storage."
        ).map { config -> config.name to config.value }.toMap()
        return storageBuilder.build(configs)
    }
}
