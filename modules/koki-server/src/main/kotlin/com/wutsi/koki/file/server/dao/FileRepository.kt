package com.wutsi.koki.file.server.dao

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : CrudRepository<FileEntity, Long> {
    @Query("SELECT F.id, L FROM FileEntity F JOIN F.labels L WHERE F.id IN :ids")
    fun findLabelsByIds(@Param("ids") ids: List<Long>): List<Array<Any>>

    fun countByTypeAndOwnerIdAndOwnerTypeAndDeleted(
        type: FileType,
        ownerId: Long,
        ownerType: ObjectType,
        deleted: Boolean,
    ): Long?
}
