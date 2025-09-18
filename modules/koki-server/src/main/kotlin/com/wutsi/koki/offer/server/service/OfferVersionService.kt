package com.wutsi.koki.offer.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.offer.server.dao.OfferVersionRepository
import com.wutsi.koki.offer.server.domain.OfferVersionEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class OfferVersionService(
    private val dao: OfferVersionRepository,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): OfferVersionEntity {
        val version = dao.findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.OFFER_VERSION_NOT_FOUND)) }

        if (version.tenantId != tenantId) {
            throw NotFoundException(Error(ErrorCode.OFFER_VERSION_NOT_FOUND))
        }
        return version
    }

    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        offerId: Long? = null,
        agentUserId: Long? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<OfferVersionEntity> {
        val jql = StringBuilder("SELECT V FROM OfferVersionEntity V WHERE V.tenantId = :tenantId")

        if (ids.isNotEmpty()) {
            jql.append(" AND V.id IN :ids")
        }
        if (offerId != null) {
            jql.append(" AND V.offer.id=:offerId")
        }
        if (agentUserId != null) {
            jql.append(" AND (V.offer.buyerAgentUserId=:agentUserId OR V.offer.sellerAgentUserId=:agentUserId)")
        }
        jql.append(" ORDER BY V.id DESC")

        val query = em.createQuery(jql.toString(), OfferVersionEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (offerId != null) {
            query.setParameter("offerId", offerId)
        }
        if (agentUserId != null) {
            query.setParameter("agentUserId", agentUserId)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }
}
