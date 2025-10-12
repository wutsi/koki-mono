package com.wutsi.koki.file.server.dao

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : CrudRepository<FileEntity, Long> {
    fun countByTypeAndOwnerIdAndOwnerTypeAndDeleted(
        type: FileType,
        ownerId: Long,
        ownerType: ObjectType,
        deleted: Boolean,
    ): Long?
}
