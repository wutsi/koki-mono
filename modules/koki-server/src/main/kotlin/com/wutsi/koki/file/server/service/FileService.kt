package com.wutsi.koki.file.server.service

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.dao.FileRepository
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.util.MimeUtils
import jakarta.persistence.EntityManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
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
    private val logger: KVLogger,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): FileEntity {
        val file = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.FILE_NOT_FOUND)) }

        if (file.tenantId != tenantId || file.deleted) {
            throw NotFoundException(Error(ErrorCode.FILE_NOT_FOUND))
        }
        return file
    }

    fun countByTypeAndOwnerIdAndOwnerType(
        type: FileType,
        ownerId: Long,
        ownerType: ObjectType,
    ): Long? {
        return dao.countByTypeAndOwnerIdAndOwnerTypeAndDeleted(type, ownerId, ownerType, false)
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
    fun create(request: CreateFileRequest, tenantId: Long): FileEntity {
        // Check the file URL already exists
        val sourceUrlHash = generateHash(request.url, request.owner)
        if (dao.findBySourceUrlHashAndDeletedAndTenantId(sourceUrlHash, false, tenantId).isNotEmpty()) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.FILE_ALREADY_EXISTS,
                    message = "File already exists: ${request.url}",
                    data = mapOf(
                        "url" to request.url,
                        "ownerId" to request.owner?.id.toString(),
                        "ownerType" to request.owner?.type.toString(),
                    )
                )
            )
        }

        // Create the file
        val file = download(request.url)
        val extension = FilenameUtils.getExtension(file.name)
        val contentType = MimeUtils.getMimeTypeFromExtension(extension)
        val url = file.inputStream().use { inputStream ->
            store(
                filename = file.name,
                contentType = contentType,
                contentLength = file.length(),
                content = inputStream,
                ownerId = request.owner?.id,
                ownerType = request.owner?.type,
                tenantId = tenantId,
            )
        }
        return dao.save(
            FileEntity(
                createdById = securityService.getCurrentUserIdOrNull(),
                tenantId = tenantId,
                name = file.name,
                url = url.toString(),
                sourceUrl = request.url,
                sourceUrlHash = sourceUrlHash,
                contentType = contentType,
                contentLength = file.length(),
                type = if (contentType.startsWith("image/")) FileType.IMAGE else FileType.FILE,
                ownerId = request.owner?.id,
                ownerType = request.owner?.type,
                status = FileStatus.UNDER_REVIEW,
            )
        )
    }

    private fun generateHash(url: String, owner: ObjectReference?): String {
        val xurl = if (url.endsWith("/")) {
            url.substring(0, url.length - 1)
        } else {
            url
        }

        val key = xurl.lowercase().trim() + (owner?.let { "-${owner.id}-${owner.type}" } ?: "")
        return DigestUtils.md5Hex(key)
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
            path = path,
            content = content,
            contentType = contentType,
            contentLength = contentLength,
        )
    }

    @Transactional
    fun delete(id: Long, tenantId: Long): FileEntity {
        val file = get(id, tenantId)
        file.deleted = true
        file.deletedAt = Date()
        file.deletedById = securityService.getCurrentUserIdOrNull()
        return dao.save(file)
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

    private fun download(urlPath: String): File {
        val url = URL(urlPath)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connect()
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val contentType = connection.contentType
                val extension = MimeUtils.getExtensionFromMimeType(contentType)
                val file = File.createTempFile(UUID.randomUUID().toString(), extension)
                connection.inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                return file
            } else {
                throw IOException("status: " + connection.responseCode)
            }
        } finally {
            connection.disconnect()
        }
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
        ).associate { config -> config.name to config.value }
        return storageBuilder.build(configs)
    }
}
