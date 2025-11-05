package com.wutsi.koki.agent.server.service

import com.wutsi.koki.agent.dto.MetricPeriod
import com.wutsi.koki.agent.server.dao.AgentMetricRepository
import com.wutsi.koki.agent.server.domain.AgentEntity
import com.wutsi.koki.agent.server.domain.AgentMetricEntity
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.TenantService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.math.max
import kotlin.math.min

@Service
class AgentMetricService(
    private val agentService: AgentService,
    private val tenantService: TenantService,
    private val dao: AgentMetricRepository,
    private val em: EntityManager,
) {
    @Transactional
    fun updateMetrics(agent: AgentEntity) {
        val tenant = tenantService.get(agent.tenantId)
        ListingType.entries
            .filter { type -> type != ListingType.UNKNOWN }
            .forEach { type ->
                MetricPeriod.entries
                    .filter { period -> period != MetricPeriod.UNKNOWN }
                    .forEach { period -> updateMetric(agent, type, period, tenant) }
            }

        val agentId = agent.id ?: -1
        agent.totalSales = total(agentId, ListingType.SALE, MetricPeriod.OVERALL)
        agent.totalRentals = total(agentId, ListingType.RENTAL, MetricPeriod.OVERALL)
        agent.totalTransactions = (agent.totalSales ?: 0) + (agent.totalRentals ?: 0)
        agent.past12mSales = total(agentId, ListingType.SALE, MetricPeriod.PAST_12M)
        agent.past12mRentals = total(agentId, ListingType.RENTAL, MetricPeriod.OVERALL)
        agent.past12mTransactions = (agent.past12mSales ?: 0) + (agent.past12mRentals ?: 0)
        agentService.save(agent)
    }

    private fun updateMetric(agent: AgentEntity, listingType: ListingType, period: MetricPeriod, tenant: TenantEntity) {
        val agentId = agent.id ?: -1
        val metric = dao.findByAgentIdAndListingTypeAndPeriod(agentId, listingType, period)
            ?: AgentMetricEntity(
                agentId = agentId,
                listingType = listingType,
                period = period,
                tenantId = agent.tenantId,
            )

        val seller = query(agent, OfferParty.SELLER, listingType, period)
        val buyer = query(agent, OfferParty.BUYER, listingType, period)
        metric.total = seller.total + buyer.total
        metric.totalPrice = seller.totalPrice + buyer.totalPrice
        metric.maxPrice = max(seller.maxPrice, buyer.maxPrice)
        metric.minPrice = if (seller.minPrice == 0L) {
            buyer.minPrice
        } else if (buyer.minPrice == 0L) {
            seller.minPrice
        } else {
            min(seller.minPrice, buyer.minPrice)
        }
        metric.averagePrice = if (metric.totalPrice == 0L) {
            0
        } else {
            (seller.totalPrice + buyer.totalPrice) / metric.total
        }
        metric.currency = tenant.currency

        if (metric.total == 0L) {
            if (metric.id != null) {
                dao.delete(metric)
            }
        } else {
            dao.save(metric)
        }
    }

    private fun query(agent: AgentEntity, party: OfferParty, type: ListingType, period: MetricPeriod): MetricDto {
        val jql = buildQuery(agent, party, period)
        val query = em.createQuery(jql, List::class.java)

        query.setParameter("listingType", type)
        when (period) {
            MetricPeriod.PAST_12M ->
                query.setParameter("date", DateUtils.addMonths(Date(), -12))

            else -> {}
        }
        try {
            val result = query.singleResult
            return MetricDto(
                total = result[0] as Long,
                minPrice = result[1] as Long,
                maxPrice = result[2] as Long,
                averagePrice = result[3] as Double,
                totalPrice = result[4] as Long,
            )
        } catch (ex: Exception) {
            return MetricDto()
        }
    }

    private fun buildQuery(agent: AgentEntity, party: OfferParty, period: MetricPeriod): String {
        val sb = StringBuilder(
            """
                SELECT COUNT(L), MIN(L.salePrice), MAX(L.salePrice), AVG(L.salePrice), SUM(L.salePrice)
                FROM ListingEntity L
                WHERE L.listingType =: listingType
            """.trimIndent()
        )
        when (party) {
            OfferParty.BUYER ->
                sb.append(" AND L.buyerAgentUserId=${agent.userId} AND L.buyerAgentUserId != L.sellerAgentUserId")

            else ->
                sb.append(" AND L.sellerAgentUserId=${agent.userId}")
        }
        when (period) {
            MetricPeriod.PAST_12M ->
                sb.append(" AND L.soldAt >= :date")

            else -> {}
        }
        return sb.toString()
    }

    private fun total(agentId: Long, listingType: ListingType, period: MetricPeriod): Long? {
        return dao.findByAgentIdAndListingTypeAndPeriod(agentId, listingType, period)?.total
    }
}

data class MetricDto(
    val total: Long = 0,
    val minPrice: Long = 0,
    val maxPrice: Long = 0,
    val averagePrice: Double = 0.0,
    val totalPrice: Long = 0,
)
