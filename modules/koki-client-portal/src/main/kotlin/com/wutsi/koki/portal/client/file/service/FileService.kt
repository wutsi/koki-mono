package com.wutsi.koki.portal.client.file.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.UploadFileResponse
import com.wutsi.koki.portal.client.file.mapper.FileMapper
import com.wutsi.koki.portal.client.file.model.FileModel
import com.wutsi.koki.sdk.KokiFiles
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileService(
    private val koki: KokiFiles,
    private val mapper: FileMapper,
) {
    fun file(id: Long): FileModel {
        val file = koki.file(id).file
        return mapper.toFileModel(file)
    }

    fun files(
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
            offset = offset
        ).files
        if (files.isEmpty()) {
            return emptyList()
        }

        return files.map { file -> mapper.toFileModel(file) }
    }

    fun upload(ownerId: Long?, ownerType: ObjectType?, file: MultipartFile): UploadFileResponse {
        return koki.upload(ownerId, ownerType, FileType.FILE, file)
    }

    fun delete(id: Long) {
        koki.delete(id)
    }

    fun uploadUrl(ownerId: Long, ownerType: ObjectType): String {
        return koki.uploadUrl(ownerId, ownerType, FileType.FILE)
    }
}
