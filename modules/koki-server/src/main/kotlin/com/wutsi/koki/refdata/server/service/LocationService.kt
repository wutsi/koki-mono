package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.refdata.server.dao.UnitRepository
import com.wutsi.koki.refdata.server.domain.UnitEntity
import org.springframework.stereotype.Service

@Service
class UnitService(private val dao: UnitRepository) {
    fun all(): List<UnitEntity> {
        return dao.findAll().toList()
    }
}
