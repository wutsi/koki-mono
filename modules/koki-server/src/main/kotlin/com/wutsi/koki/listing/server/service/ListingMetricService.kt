package com.wutsi.koki.listing.server.service

import com.wutsi.koki.listing.dto.ListingMetricDimension
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.listing.server.domain.ListingMetricEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ListingMetricService(
    private val em: EntityManager,
) {
    fun search(
        tenantId: Long,
        neighbourhoodId: Long? = null,
        sellerAgentUserId: Long? = null,
        cityId: Long? = null,
        bedrooms: Int? = null,
        propertyCategory: PropertyCategory? = null,
        listingType: ListingType? = null,
        listingStatus: ListingStatus? = null,
        dimension: ListingMetricDimension? = null,
    ): List<ListingMetricEntity> {
        val jql = StringBuilder("SELECT NM FROM ListingMetricEntity NM WHERE NM.tenantId = :tenantId")

        // WHERE
        if (neighbourhoodId != null) {
            jql.append(" AND NM.neighborhoodId = :neighbourhoodId")
        }
        if (sellerAgentUserId != null) {
            jql.append(" AND NM.sellerAgentUserId = :sellerAgentUserId")
        }
        if (cityId != null) {
            jql.append(" AND NM.cityId = :cityId")
        }
        if (bedrooms != null) {
            jql.append(" AND NM.bedrooms = :bedrooms")
        }
        if (propertyCategory != null) {
            jql.append(" AND NM.propertyCategory = :propertyCategory")
        }
        if (listingType != null) {
            jql.append(" AND NM.listingType = :listingType")
        }
        if (listingStatus != null) {
            jql.append(" AND NM.listingStatus = :listingStatus")
        }

        // PARAMETERS
        val query = em.createQuery(jql.toString(), ListingMetricEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (neighbourhoodId != null) {
            query.setParameter("neighbourhoodId", neighbourhoodId)
        }
        if (sellerAgentUserId != null) {
            query.setParameter("sellerAgentUserId", sellerAgentUserId)
        }
        if (cityId != null) {
            query.setParameter("cityId", cityId)
        }
        if (bedrooms != null) {
            query.setParameter("bedrooms", bedrooms)
        }
        if (propertyCategory != null) {
            query.setParameter("propertyCategory", propertyCategory)
        }
        if (listingType != null) {
            query.setParameter("listingType", listingType)
        }
        if (listingStatus != null) {
            query.setParameter("listingStatus", listingStatus)
        }

        val metrics = query.resultList
        if (metrics.size <= 1 || dimension == null) {
            return metrics
        }

        return metrics.filter { metrics -> metrics.listingType != ListingType.UNKNOWN }
            .groupBy { metric -> metric.listingType }
            .values
            .flatMap { metrics -> groupBy(metrics, dimension) }
    }

    @Transactional
    fun aggregate(tenantId: Long): Int {
        val sql = """
            INSERT INTO T_LISTING_METRIC (
                tenant_fk,
                neighbourhood_fk,
                seller_agent_user_fk,
                city_fk,
                bedrooms,
                property_category,
                listing_status,
                listing_type,
                total_listings,
                min_price,
                max_price,
                total_price,
                average_price,
                average_lot_area,
                price_per_square_meter,
                currency,
                created_at,
                modified_at
            )
            SELECT
                tenant_fk,
                neighbourhood_fk,
                seller_agent_user_fk,
                city_fk,
                bedrooms,
                property_category,
                status AS listing_status,
                listing_type,
                COUNT(*) AS total_listings,
                MIN(price) AS min_price,
                MAX(price) AS max_price,
                SUM(price) AS total_price,
                AVG(price) AS average_price,
                AVG(lot_area) AS average_lot_area,
                CASE
                    WHEN AVG(property_area) > 0 THEN AVG(price) / AVG(property_area)
                    ELSE 0
                END AS price_per_square_meter,
                currency,
                NOW() AS created_at,
                NOW() AS modified_at
            FROM T_LISTING
            WHERE
                tenant_fk = :tenantId
                AND (status=${ListingStatus.ACTIVE.ordinal} OR status=${ListingStatus.SOLD.ordinal} OR status=${ListingStatus.RENTED.ordinal})
                AND neighbourhood_fk IS NOT NULL
                AND city_fk IS NOT NULL
            GROUP BY
                tenant_fk,
                neighbourhood_fk,
                seller_agent_user_fk,
                city_fk,
                bedrooms,
                property_category,
                status,
                listing_type,
                currency
            ON DUPLICATE KEY UPDATE
                total_listings = VALUES(total_listings),
                min_price = VALUES(min_price),
                max_price = VALUES(max_price),
                total_price = VALUES(total_price),
                average_price = VALUES(average_price),
                average_lot_area = VALUES(average_lot_area),
                price_per_square_meter = VALUES(price_per_square_meter),
                currency = VALUES(currency),
                modified_at = NOW()
        """.trimIndent()

        val query = em.createNativeQuery(sql)
        query.setParameter("tenantId", tenantId)
        return query.executeUpdate()
    }

    private fun groupBy(
        metrics: List<ListingMetricEntity>,
        dimension: ListingMetricDimension?,
    ): List<ListingMetricEntity> {
        if (metrics.isEmpty()) {
            return emptyList()
        }

        val groups = when (dimension) {
            ListingMetricDimension.NEIGHBORHOOD -> metrics.groupBy { it.neighborhoodId }
            ListingMetricDimension.CITY -> metrics.groupBy { it.cityId }
            ListingMetricDimension.SELLER_AGENT -> metrics.groupBy { it.sellerAgentUserId }
            ListingMetricDimension.BEDROOMS -> metrics.groupBy { it.bedrooms }
            ListingMetricDimension.PROPERTY_CATEGORY -> metrics.groupBy { it.propertyCategory }
            else -> return metrics
        }

        return groups.map { entry -> sum(entry.value, dimension) }
    }

    private fun sum(metrics: List<ListingMetricEntity>, dimension: ListingMetricDimension): ListingMetricEntity {
        val result = metrics.reduce { acc, metric ->
            acc.copy(
                currency = metric.currency,
                listingType = metric.listingType,
                listingStatus = metric.listingStatus,

                cityId = if (dimension == ListingMetricDimension.CITY) metric.cityId else -1,
                neighborhoodId = if (dimension == ListingMetricDimension.NEIGHBORHOOD) metric.neighborhoodId else -1,
                sellerAgentUserId = if (dimension == ListingMetricDimension.SELLER_AGENT) metric.sellerAgentUserId else -1,
                bedrooms = if (dimension == ListingMetricDimension.BEDROOMS) metric.bedrooms else -1,
                propertyCategory = if (dimension == ListingMetricDimension.PROPERTY_CATEGORY) metric.propertyCategory else PropertyCategory.UNKNOWN,

                totalListings = acc.totalListings + metric.totalListings,
                minPrice = minOf(acc.minPrice, metric.minPrice),
                maxPrice = maxOf(acc.maxPrice, metric.maxPrice),
                totalPrice = acc.totalPrice + metric.totalPrice,
            )
        }
        val count = metrics.count { metric -> metric.averageLotArea != null }
        val lotArea =
            metrics.filter { metric -> metric.averageLotArea != null }.sumOf { metric -> metric.averageLotArea!! }
        val averagePrice = result.totalPrice / result.totalListings
        val averageLotArea = if (count > 0) lotArea / count else null
        return result.copy(
            averagePrice = averagePrice,
            averageLotArea = averageLotArea,
            pricePerSquareMeter = averageLotArea?.let { averagePrice / averageLotArea }
        )
    }
}
