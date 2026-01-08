package com.wutsi.koki.listing.server.service

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.listing.server.domain.NeighbourhoodMetricEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class NeighbourhoodMetricService(
    private val em: EntityManager,
) {
    fun search(
        tenantId: Long,
        neighbourhoodId: Long? = null,
        propertyCategory: PropertyCategory? = null,
        listingType: ListingType? = null,
        listingStatus: ListingStatus? = null
    ): List<NeighbourhoodMetricEntity> {
        val jql = StringBuilder("SELECT NM FROM NeighbourhoodMetricEntity NM WHERE NM.tenantId = :tenantId")

        // WHERE
        if (neighbourhoodId != null) {
            jql.append(" AND NM.neighborhoodId = :neighbourhoodId")
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
        val query = em.createQuery(jql.toString(), NeighbourhoodMetricEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (neighbourhoodId != null) {
            query.setParameter("neighbourhoodId", neighbourhoodId)
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

        return query.resultList
    }
}
