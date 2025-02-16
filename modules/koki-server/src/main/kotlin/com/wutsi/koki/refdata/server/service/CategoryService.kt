package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.refdata.dto.CategoryType
import com.wutsi.koki.refdata.server.dao.CategoryRepository
import com.wutsi.koki.refdata.server.domain.CategoryEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class CategoryService(
    private val dao: CategoryRepository,
    private val em: EntityManager,
) {
    fun getByIdOrNull(id: Long): CategoryEntity? {
        return dao.findById(id).getOrNull()
    }

    fun getByType(type: CategoryType): List<CategoryEntity> {
        return dao.findByType(type)
    }

    @Transactional
    fun save(category: CategoryEntity): CategoryEntity {
        return dao.save(category)
    }

    fun search(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        parentId: Long? = null,
        type: CategoryType? = null,
        active: Boolean? = null,
        level: Int? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<CategoryEntity> {
        val jql = StringBuilder("SELECT C FROM CategoryEntity C WHERE C.id>0")

        if (keyword != null) {
            jql.append(" AND UPPER(C.name) LIKE :keyword")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND C.id IN :ids")
        }
        if (parentId != null) {
            jql.append(" AND C.parentId = :parentId")
        }
        if (type != null) {
            jql.append(" AND C.type = :type")
        }
        if (active != null) {
            jql.append(" AND C.active = :active")
        }
        if (level != null) {
            jql.append(" AND C.level = :level")
        }

        val query = em.createQuery(jql.toString(), CategoryEntity::class.java)
        if (keyword != null) {
            query.setParameter("keyword", "${keyword.uppercase()}%")
        }
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (parentId != null) {
            query.setParameter("parentId", parentId)
        }
        if (type != null) {
            query.setParameter("type", type)
        }
        if (active != null) {
            query.setParameter("active", active)
        }
        if (level != null) {
            query.setParameter("level", level)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
