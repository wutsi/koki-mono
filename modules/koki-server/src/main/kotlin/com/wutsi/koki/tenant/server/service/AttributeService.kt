package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.tenant.server.dao.AttributeRepository
import com.wutsi.koki.tenant.server.domain.AttributeEntity
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
open class AttributeService(
    private val dao: AttributeRepository
) {
    fun search(names: List<String>, tenantId: Long): List<AttributeEntity> {
        return if (names.isEmpty()) {
            dao.findByTenantId(tenantId)
        } else {
            dao.findByTenantIdAndNameIn(tenantId, names)
        }
    }

    fun getByName(name: String, tenantId: Long): AttributeEntity {
        val attributes = search(listOf(name), tenantId)
        if (attributes.isEmpty()) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.ATTRIBUTE_NOT_FOUND,
                    parameter = Parameter(
                        value = name
                    )
                )
            )
        } else {
            return attributes.first()
        }
    }

    @Transactional
    open fun save(attribute: AttributeEntity): AttributeEntity {
        attribute.modifiedAt = Date()
        return dao.save(attribute)
    }
}
