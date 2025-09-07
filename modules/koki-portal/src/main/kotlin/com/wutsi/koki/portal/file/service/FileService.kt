package com.wutsi.koki.portal.file.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.portal.file.mapper.FileMapper
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiFiles
import org.springframework.stereotype.Service

@Service
class FileService(
    private val koki: KokiFiles,
    private val userService: UserService,
    private val uploadUrlProvider: FileUploadUrlProvider,
    private val mapper: FileMapper,
) {
    fun get(id: Long): FileModel {
        val file = koki.file(id).file
        val createdBy = file.createdById?.let { id -> userService.get(id) }

        return mapper.toFileModel(
            entity = file,
            createdBy = createdBy,
        )
    }

    fun search(
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        type: FileType? = null,
        status: FileStatus? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<FileModel> {
        val files = koki.files(
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            type = type,
            status = status,
            limit = limit,
            offset = offset,
        ).files
        if (files.isEmpty()) {
            return emptyList()
        }

        val userIds = files.mapNotNull { file -> file.createdById }.toSet()
        val userMap = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds.toList(),
                limit = userIds.size
            ).associateBy { file -> file.id }
        }

        return files.map { file ->
            mapper.toFileModel(
                entity = file,
                createdBy = file.createdById?.let { id -> userMap[id] },
            )
        }
    }

    fun uploadUrl(
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        type: FileType,
    ): String {
        return uploadUrlProvider.get(
            ownerId = ownerId,
            ownerType = ownerType,
            type = type,
        )
    }

    fun delete(id: Long) {
        koki.delete(id)
    }
}
