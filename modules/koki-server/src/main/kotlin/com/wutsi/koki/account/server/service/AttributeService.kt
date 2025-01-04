package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.NotFoundException
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
