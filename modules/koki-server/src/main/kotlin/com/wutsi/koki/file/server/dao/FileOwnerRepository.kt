package com.wutsi.koki.file.server.dao

import com.wutsi.koki.file.server.domain.FileOwnerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FileOwnerRepository : CrudRepository<FileOwnerEntity, Long> {
    fun findByFileId(fileId: Long): List<FileOwnerEntity>
}
