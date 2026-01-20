package com.wutsi.koki.webscraping.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.webscraping.dto.GetWebpageResponse
import com.wutsi.koki.webscraping.dto.SearchWebpagesResponse
import com.wutsi.koki.webscraping.server.dao.WebpageRepository
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.domain.WebsiteEntity
import com.wutsi.koki.webscraping.server.mapper.WebpageMapper
import jakarta.persistence.EntityManager
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WebpageService(
    private val dao: WebpageRepository,
    private val mapper: WebpageMapper,
    private val em: EntityManager,
) {
    fun getByUrlHash(urlHash: String, tenantId: Long): WebpageEntity? {
        return dao.findByUrlHashAndTenantId(urlHash, tenantId)
    }

    fun search(
        websiteId: Long?,
        active: Boolean?,
        limit: Int,
        offset: Int,
        tenantId: Long
    ): SearchWebpagesResponse {
        // Build JPQL query
        val jql = StringBuilder("SELECT W FROM WebpageEntity W WHERE W.tenantId = :tenantId")

        // WHERE clauses
        if (websiteId != null) {
            jql.append(" AND W.websiteId = :websiteId")
        }
        if (active != null) {
            jql.append(" AND W.active = :active")
        }

        // Create query
        val query = em.createQuery(jql.toString(), WebpageEntity::class.java)

        // Set parameters
        query.setParameter("tenantId", tenantId)
        if (websiteId != null) {
            query.setParameter("websiteId", websiteId)
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        // Pagination
        query.firstResult = offset
        query.maxResults = limit

        val webpages = query.resultList

        return SearchWebpagesResponse(
            webpages = webpages.map { mapper.toWebpageSummary(it) }
        )
    }

    fun get(id: Long, tenantId: Long): GetWebpageResponse {
        val webpage = dao.findByIdAndTenantId(id, tenantId)
            ?: throw NotFoundException(
                error = Error(
                    code = ErrorCode.WEBPAGE_NOT_FOUND,
                    message = "Webpage not found"
                )
            )

        return GetWebpageResponse(
            webpage = mapper.toWebpage(webpage)
        )
    }

    fun new(
        website: WebsiteEntity,
        url: String,
        images: List<String>,
        content: String?,
    ): WebpageEntity {
        return WebpageEntity(
            websiteId = website.id!!,
            tenantId = website.tenantId,
            url = url,
            urlHash = generateHash(url),
            imageUrls = images,
            content = content,
            active = true,
        )
    }

    @Transactional
    fun save(webpage: WebpageEntity): WebpageEntity {
        return dao.save(webpage)
    }

    private fun generateHash(value: String): String {
        return DigestUtils.md5Hex(value.lowercase().trim())
    }
}
