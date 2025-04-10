package com.wutsi.koki.file.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.server.dao.FileOwnerRepository
import com.wutsi.koki.file.server.domain.FileOwnerEntity
import org.springframework.stereotype.Service

@Service
class FileOwnerService(private val dao: FileOwnerRepository) {
    fun findByFileIdAnAndOwnerType(fileId: Long, type: ObjectType): FileOwnerEntity? {
        return dao.findByFileIdAndOwnerType(fileId, type)
    }
}
