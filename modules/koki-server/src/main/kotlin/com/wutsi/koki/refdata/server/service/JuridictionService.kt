package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.refdata.server.dao.SalesTaxRepository
import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class SalesTaxService(
    private val dao: SalesTaxRepository,
    private val em: EntityManager,
) {
    fun getByCountry(country: String): List<SalesTaxEntity> {
        return dao.findByCountry(country)
    }

    @Transactional
    fun save(salesTax: SalesTaxEntity): SalesTaxEntity {
        return dao.save(salesTax)
    }

    fun search(
        ids: List<Long> = emptyList(),
        stateId: Long? = null,
        country: String? = null,
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<SalesTaxEntity> {
        val jql = StringBuilder("SELECT S FROM SalesTaxEntity S WHERE S.id>0")

        if (ids.isNotEmpty()) {
            jql.append(" AND S.id IN :ids")
        }
        if (stateId != null) {
            jql.append(" AND S.stateId = :stateId")
        }
        if (active != null) {
            jql.append(" AND S.active = :active")
        }
        if (country != null) {
            jql.append(" AND S.country = :country")
        }

        val query = em.createQuery(jql.toString(), SalesTaxEntity::class.java)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (stateId != null) {
            query.setParameter("stateId", stateId)
        }
        if (active != null) {
            query.setParameter("active", active)
        }
        if (country != null) {
            query.setParameter("country", country)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
