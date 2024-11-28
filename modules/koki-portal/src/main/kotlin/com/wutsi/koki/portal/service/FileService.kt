package com.wutsi.koki.portal.service

import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.portal.mapper.FileMapper
import com.wutsi.koki.portal.model.FileModel
import com.wutsi.koki.sdk.KokiFile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URL

@Service
class FileService(
    private val kokiFile: KokiFile,
    private val userService: UserService,
    private val mapper: FileMapper,
) {
    fun create(
        file: MultipartFile,
        url: URL,
        workflowInstanceId: String?,
        formId: String?
    ): String {
        return kokiFile.create(
            CreateFileRequest(
                url = url.toString(),
                contentType = file.contentType ?: "application/octet-stream",
                contentLength = file.size,
                name = file.originalFilename!!,
                workflowInstanceId = workflowInstanceId,
                formId = formId,
            )
        ).fileId
    }

    fun file(id: String): FileModel {
        val file = kokiFile.get(id).file
        val createdBy = file.createdById?.let { id -> userService.user(id) }

        return mapper.toFileModel(
            entity = file,
            createdBy = createdBy,
        )
    }

    fun files(
        ids: List<String> = emptyList(),
        workflowInstanceIds: List<String> = emptyList(),
        formIds: List<String> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<FileModel> {
        val files = kokiFile.search(
            ids = ids,
            workflowInstanceIds = workflowInstanceIds,
            formIds = formIds,
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
}
