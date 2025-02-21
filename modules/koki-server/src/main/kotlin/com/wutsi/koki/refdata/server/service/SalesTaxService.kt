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
        juridictionIds: List<Long> = emptyList(),
        active: Boolean? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<SalesTaxEntity> {
        val jql = StringBuilder("SELECT S FROM SalesTaxEntity S WHERE S.id>0")

        if (ids.isNotEmpty()) {
            jql.append(" AND S.id IN :ids")
        }
        if (juridictionIds.isNotEmpty()) {
            jql.append(" AND S.juridiction.id IN :juridictionIds")
        }
        if (active != null) {
            jql.append(" AND S.active = :active")
        }

        val query = em.createQuery(jql.toString(), SalesTaxEntity::class.java)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (juridictionIds.isNotEmpty()) {
            query.setParameter("juridictionIds", juridictionIds)
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
