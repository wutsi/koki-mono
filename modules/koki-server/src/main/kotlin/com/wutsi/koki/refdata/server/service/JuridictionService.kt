package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.refdata.server.dao.JuridictionRepository
import com.wutsi.koki.refdata.server.domain.JuridictionEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class JuridictionService(
    private val dao: JuridictionRepository,
    private val em: EntityManager,
) {
    fun getByIdOrNull(id: Long): JuridictionEntity? {
        return dao.findById(id).getOrNull()
    }

    fun getByCountry(country: String): List<JuridictionEntity> {
        return dao.findByCountry(country)
    }

    @Transactional
    fun save(salesTax: JuridictionEntity): JuridictionEntity {
        return dao.save(salesTax)
    }

    fun search(
        ids: List<Long> = emptyList(),
        stateId: Long? = null,
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<JuridictionEntity> {
        val jql = StringBuilder("SELECT J FROM JuridictionEntity J WHERE J.id>0")

        if (ids.isNotEmpty()) {
            jql.append(" AND J.id IN :ids")
        }
        if (stateId != null) {
            jql.append(" AND J.stateId = :stateId")
        }
        if (country != null) {
            jql.append(" AND J.country = :country")
        }

        val query = em.createQuery(jql.toString(), JuridictionEntity::class.java)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (stateId != null) {
            query.setParameter("stateId", stateId)
        }
        if (country != null) {
            query.setParameter("country", country)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
