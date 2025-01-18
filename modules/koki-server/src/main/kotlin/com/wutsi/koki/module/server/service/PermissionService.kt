package com.wutsi.koki.module.server.service

import com.wutsi.koki.module.server.domain.PermissionEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class PermissionService(
    private val em: EntityManager
) {
    fun search(
        ids: List<Long> = emptyList(),
        moduleIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<PermissionEntity> {
        val jql = StringBuilder("SELECT P FROM PermissionEntity P WHERE 1=1")
        if (ids.isNotEmpty()) {
            jql.append(" AND P.id IN :ids")
        }
        if (moduleIds.isNotEmpty()) {
            jql.append(" AND P.moduleId IN :moduleIds")
        }

        val query = em.createQuery(jql.toString(), PermissionEntity::class.java)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (moduleIds.isNotEmpty()) {
            query.setParameter("moduleIds", moduleIds)
        }
        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
