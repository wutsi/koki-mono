package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.refdata.server.dao.AmenityRepository
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class AmenityService(
    private val dao: AmenityRepository,
    private val em: EntityManager,
) {
    fun all(): List<AmenityEntity> {
        return dao.findAll().toList()
    }

    fun get(id: Long): AmenityEntity {
        return getByIdOrNull(id)
            ?: throw NotFoundException(error = Error(ErrorCode.AMENITY_NOT_FOUND))
    }

    fun getByIdOrNull(id: Long): AmenityEntity? {
        return dao.findById(id).getOrNull()
    }

    @Transactional
    fun save(amenity: AmenityEntity): AmenityEntity {
        return dao.save(amenity)
    }

    fun search(
        ids: List<Long> = emptyList(),
        categoryId: Long? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<AmenityEntity> {
        val jql = StringBuilder("SELECT C FROM AmenityEntity C WHERE C.id>0")

        if (ids.isNotEmpty()) {
            jql.append(" AND C.id IN :ids")
        }
        if (categoryId != null) {
            jql.append(" AND C.categoryId = :categoryId")
        }
        if (active != null) {
            jql.append(" AND C.active = :active")
        }

        val query = em.createQuery(jql.toString(), AmenityEntity::class.java)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId)
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
