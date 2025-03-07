package com.wutsi.koki.file.server.service

import com.lowagie.text.pdf.PdfFileSpecification.url
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.dao.FileOwnerRepository
import com.wutsi.koki.file.server.dao.FileRepository
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.domain.FileOwnerEntity
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.StorageType
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.net.URL
import java.net.URLEncoder
import java.util.Date
import java.util.UUID

@Service
class FileService(
    private val dao: FileRepository,
    private val ownerDao: FileOwnerRepository,
    private val storageBuilder: StorageServiceBuilder,
    private val configurationService: ConfigurationService,
    private val securityService: SecurityService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): FileEntity {
        val file = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.FILE_NOT_FOUND)) }

        if (file.tenantId != tenantId || file.deleted) {
            throw NotFoundException(Error(ErrorCode.FILE_NOT_FOUND))
        }
        return file
    }

    fun search(
        tenantId: Long,
        ids: List<String> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<FileEntity> {
        val jql = StringBuilder("SELECT F FROM FileEntity AS F")
        if (ownerId != null || ownerType != null) {
            jql.append(" JOIN F.fileOwners AS O")
        }

        jql.append(" WHERE F.deleted=false AND F.tenantId=:tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND F.id IN :ids")
        }
        if (ownerId != null) {
            jql.append(" AND O.ownerId = :ownerId")
        }
        if (ownerType != null) {
            jql.append(" AND O.ownerType = :ownerType")
        }
        jql.append(" ORDER BY LOWER(F.name)")

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

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun upload(
        userId: Long?,
        file: MultipartFile,
        ownerId: Long?,
        ownerType: ObjectType?,
        tenantId: Long,
    ): FileEntity {
        // Store
        val url = store(
            filename = URLEncoder.encode(file.originalFilename, "utf-8"),
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
            tenantId = tenantId
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
        tenantId: Long,
    ): FileEntity {
        // Create the file
        val file = dao.save(
            FileEntity(
                createdById = userId ?: securityService.getCurrentUserIdOrNull(),
                tenantId = tenantId,
                name = filename,
                url = url.toString(),
                contentType = contentType ?: "application/octet-stream",
                contentLength = contentLength,
                createdAt = Date(),
            )
        )

        // Link with owner
        if (ownerId != null && ownerType != null) {
            ownerDao.save(
                FileOwnerEntity(
                    fileId = file.id!!,
                    ownerId = ownerId,
                    ownerType = ownerType,
                )
            )
        }
        return file
    }

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
    fun save(files: List<FileEntity>) {
        dao.saveAll(files)
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
            tenantId = tenantId,
            keyword = "storage."
        ).map { config -> config.name to config.value }
            .toMap()

        val type = configs.get(ConfigurationName.STORAGE_TYPE)
            ?.let { value -> StorageType.valueOf(value.uppercase()) }
            ?: StorageType.KOKI

        return storageBuilder.build(type, configs)
    }
}
