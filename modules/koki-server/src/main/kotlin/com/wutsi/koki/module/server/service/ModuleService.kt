package com.wutsi.koki.module.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.module.server.domain.ModuleEntity
import com.wutsi.koki.note.server.dao.ModuleRepository
import org.springframework.stereotype.Service

@Service
class ModuleService(
    private val dao: ModuleRepository
) {
    fun get(id: Long): ModuleEntity {
        return dao.findById(id)
            .orElseThrow { NotFoundException(error = Error(ErrorCode.MODULE_NOT_FOUND)) }
    }

    fun search(): List<ModuleEntity> {
        return dao.findAll().toList()
    }
}
