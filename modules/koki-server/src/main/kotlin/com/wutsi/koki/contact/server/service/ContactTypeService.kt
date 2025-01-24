package com.wutsi.koki.contact.server.service

import com.wutsi.koki.contact.server.dao.ContactTypeRepository
import com.wutsi.koki.contact.server.domain.ContactTypeEntity
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.NotFoundException
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class ContactTypeService(
    private val dao: ContactTypeRepository,
    private var em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): ContactTypeEntity {
        val contactType = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.CONTACT_TYPE_NOT_FOUND)) }

        if (contactType.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.CONTACT_TYPE_NOT_FOUND))
        }
        return contactType
    }

    fun getByName(name: String, tenantId: Long): ContactTypeEntity {
        val roles = search(
            names = listOf(name), tenantId = tenantId, limit = 1
        )
        if (roles.isEmpty()) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.CONTACT_TYPE_NOT_FOUND, parameter = Parameter(
                        value = name
                    )
                )
            )
        } else {
            return roles.first()
        }
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        names: List<String> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<ContactTypeEntity> {
        val jql = StringBuilder("SELECT T FROM ContactTypeEntity AS T WHERE T.tenantId = :tenantId")
        if (ids.isNotEmpty()) {
            jql.append(" AND T.id IN :ids")
        }
        if (names.isNotEmpty()) {
            jql.append(" AND T.name IN :names")
        }
        if (active != null) {
            jql.append(" AND T.active = :active")
        }
        jql.append(" ORDER BY T.name")

        val query = em.createQuery(jql.toString(), ContactTypeEntity::class.java)
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

    @Transactional
    fun save(contactType: ContactTypeEntity): ContactTypeEntity {
        contactType.modifiedAt = Date()
        return dao.save(contactType)
    }
}
