package com.wutsi.koki.product.server.service

import com.wutsi.koki.product.server.dao.UnitRepository
import com.wutsi.koki.product.server.domain.UnitEntity
import org.springframework.stereotype.Service

@Service
class UnitService(private val dao: UnitRepository) {
    fun all(): List<UnitEntity> {
        return dao.findAll().toList()
    }
}
