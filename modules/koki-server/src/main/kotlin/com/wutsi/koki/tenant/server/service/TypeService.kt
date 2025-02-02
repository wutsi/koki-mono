package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.dao.TypeRepository
import com.wutsi.koki.tenant.server.domain.TypeEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TypeService(
    private val dao: TypeRepository,
    private var em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): TypeEntity {
        val type = dao.findById(id).orElseThrow { NotFoundException(Error(ErrorCode.TYPE_NOT_FOUND)) }

        if (type.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.TYPE_NOT_FOUND))
        }
        return type
    }

    fun getByNameAndObjectType(name: String, objectType: ObjectType, tenantId: Long): TypeEntity? {
        return dao.findByNameIgnoreCaseAndObjectTypeAndTenantId(name, objectType, tenantId)
    }

    fun search(
        tenantId: Long,
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        objectType: ObjectType? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<TypeEntity> {
        val jql = StringBuilder("SELECT T FROM TypeEntity AS T WHERE T.tenantId = :tenantId")

        if (keyword != null) {
            jql.append(" AND (UPPER(T.name) LIKE :keyword OR UPPER(T.title) LIKE :keyword) ")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND T.id IN :ids")
        }
        if (objectType != null) {
            jql.append(" AND T.objectType = :objectType")
        }
        if (active != null) {
            jql.append(" AND T.active = :active")
        }
        jql.append(" ORDER BY T.title, T.name")

        val query = em.createQuery(jql.toString(), TypeEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (keyword != null) {
            query.setParameter("keyword", "%${keyword.uppercase()}%")
        }
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (objectType != null) {
            query.setParameter("objectType", objectType)
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    fun save(type: TypeEntity): TypeEntity {
        type.modifiedAt = Date()
        return dao.save(type)
    }
}
