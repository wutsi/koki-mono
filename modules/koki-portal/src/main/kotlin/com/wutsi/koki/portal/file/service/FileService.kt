package com.wutsi.koki.portal.file.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.portal.file.mapper.FileMapper
import com.wutsi.koki.portal.file.model.FileModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiFiles
import org.springframework.stereotype.Service

@Service
class FileService(
    private val koki: KokiFiles,
    private val userService: UserService,
    private val mapper: FileMapper,
) {
    fun file(id: Long): FileModel {
        val file = koki.file(id).file
        val createdBy = file.createdById?.let { id -> userService.user(id) }

        return mapper.toFileModel(
            entity = file,
            createdBy = createdBy,
        )
    }

    fun files(
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<FileModel> {
        val files = koki.files(
            ids = ids,
            ownerId = ownerId,
            ownerType = ownerType,
            limit = limit,
            offset = offset
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
    ): String {
        return koki.uploadUrl(
            ownerId = ownerId,
            ownerType = ownerType,
        )
    }

    fun delete(id: Long) {
        koki.delete(id)
    }
}
