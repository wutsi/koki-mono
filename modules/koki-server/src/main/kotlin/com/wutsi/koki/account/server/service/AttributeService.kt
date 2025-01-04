package com.wutsi.koki.account.server.service

import com.wutsi.koki.account.server.dao.AttributeRepository
import com.wutsi.koki.account.server.domain.AttributeEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.NotFoundException
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
open class AttributeService(
    private val dao: AttributeRepository,
    private var em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): AttributeEntity {
        val attribute = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.ATTRIBUTE_NOT_FOUND)) }

        if (attribute.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.ATTRIBUTE_NOT_FOUND))
        }
        return attribute
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<AttributeEntity> {
        val jql = StringBuilder("SELECT A FROM AttributeEntity A WHERE A.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND A.id IN :ids")
        }
        if (names.isNotEmpty()) {
            jql.append(" AND A.name IN :names")
        }
        if (active != null) {
            jql.append(" AND A.active = :active")
        }
        jql.append(" ORDER BY A.name")

        val query = em.createQuery(jql.toString(), AttributeEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (names.isNotEmpty()) {
            query.setParameter("names", names)
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    fun getByName(name: String, tenantId: Long): AttributeEntity {
        val attributes = search(names = listOf(name), tenantId = tenantId, limit = 1)
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
