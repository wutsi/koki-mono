package com.wutsi.koki.webscraping.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.webscraping.dto.CreateWebsiteRequest
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteRequest
import com.wutsi.koki.webscraping.dto.UpdateWebsiteRequest
import com.wutsi.koki.webscraping.server.dao.WebsiteRepository
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.domain.WebsiteEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class WebsiteService(
    private val dao: WebsiteRepository,
    private val webscraper: WebscaperService,
    private val http: Http,
    private val em: EntityManager,
) {
    @Transactional
    fun create(request: CreateWebsiteRequest, tenantId: Long): WebsiteEntity {
        val baseUrlHash = http.hash(request.baseUrl)

        // Check if website already exists
        val existing = dao.findByBaseUrlHashAndTenantId(baseUrlHash, tenantId)
        if (existing != null) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.WEBSITE_DUPLICATE_BASE_URL,
                    message = "A website with this base URL already exists"
                )
            )
        }

        // Create website
        return dao.save(
            WebsiteEntity(
                tenantId = tenantId,
                userId = request.userId,
                baseUrl = request.baseUrl,
                baseUrlHash = baseUrlHash,
                listingUrlPrefix = request.listingUrlPrefix,
                contentSelector = request.contentSelector,
                imageSelector = request.imageSelector,
                homeUrls = request.homeUrls,
                active = request.active,
                createdAt = Date(),
            )
        )
    }

    @Transactional
    fun update(id: Long, request: UpdateWebsiteRequest, tenantId: Long) {
        val website = get(id, tenantId)

        website.listingUrlPrefix = request.listingUrlPrefix
        website.contentSelector = request.contentSelector
        website.imageSelector = request.imageSelector
        website.active = request.active
        website.homeUrls = request.homeUrls

        dao.save(website)
    }

    fun get(id: Long, tenantId: Long): WebsiteEntity {
        val website = dao.findByIdAndTenantId(id, tenantId)
            ?: throw NotFoundException(
                error = Error(
                    code = ErrorCode.WEBSITE_NOT_FOUND,
                    message = "Website not found"
                )
            )
        return website
    }

    fun scrape(websiteId: Long, request: ScrapeWebsiteRequest, tenantId: Long): List<WebpageEntity> {
        val website = get(websiteId, tenantId)
        return webscraper.scrape(website, request)
    }

    fun search(
        ids: List<Long>?,
        userIds: List<Long>?,
        active: Boolean?,
        limit: Int,
        offset: Int,
        tenantId: Long
    ): List<WebsiteEntity> {
        // Build JPQL query
        val jql = StringBuilder("SELECT W FROM WebsiteEntity W WHERE W.tenantId = :tenantId")

        // WHERE clauses
        if (!ids.isNullOrEmpty()) {
            jql.append(" AND W.id IN :ids")
        }
        if (!userIds.isNullOrEmpty()) {
            jql.append(" AND W.userId IN :userIds")
        }
        if (active != null) {
            jql.append(" AND W.active = :active")
        }

        // Create query
        val query = em.createQuery(jql.toString(), WebsiteEntity::class.java)

        // Set parameters
        query.setParameter("tenantId", tenantId)
        if (!ids.isNullOrEmpty()) {
            query.setParameter("ids", ids)
        }
        if (!userIds.isNullOrEmpty()) {
            query.setParameter("userIds", userIds)
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        // Pagination
        query.firstResult = offset
        query.maxResults = limit

        return query.resultList
    }
}
