package com.wutsi.koki.webscraping.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.AIListingService
import com.wutsi.koki.listing.server.service.ai.ListingAgentFactory
import com.wutsi.koki.listing.server.service.ai.ListingLocationExtractorResult
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.webscraping.server.dao.WebpageRepository
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.domain.WebsiteEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper
import java.util.Date

@Service
class WebpageService(
    private val dao: WebpageRepository,
    private val em: EntityManager,
    private val tenantService: TenantService,
    private val listingAgentFactory: ListingAgentFactory,
    private val locationService: LocationService,
    private val aiListingService: AIListingService,
    private val jsonMapper: JsonMapper,
    private val http: Http,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebpageService::class.java)!!
    }

    fun getByUrlHash(urlHash: String, tenantId: Long): WebpageEntity? {
        return dao.findByUrlHashAndTenantId(urlHash, tenantId)
    }

    fun search(
        websiteId: Long?,
        listingId: Long?,
        active: Boolean?,
        limit: Int,
        offset: Int,
        tenantId: Long
    ): List<WebpageEntity> {
        // Build JPQL query
        val jql = StringBuilder("SELECT W FROM WebpageEntity W WHERE W.tenantId = :tenantId")

        // WHERE clauses
        if (websiteId != null) {
            jql.append(" AND W.website.id = :websiteId")
        }
        if (listingId != null) {
            jql.append(" AND W.listingId = :listingId")
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
        if (listingId != null) {
            query.setParameter("listingId", listingId)
        }
        if (active != null) {
            query.setParameter("active", active)
        }

        // Pagination
        query.firstResult = offset
        query.maxResults = limit

        return query.resultList
    }

    fun get(id: Long, tenantId: Long): WebpageEntity {
        return dao.findByIdAndTenantId(id, tenantId)
            ?: throw NotFoundException(
                error = Error(
                    code = ErrorCode.WEBPAGE_NOT_FOUND,
                    message = "Webpage not found"
                )
            )
    }

    @Transactional
    fun save(webpage: WebpageEntity): WebpageEntity {
        webpage.updatedAt = Date()
        return dao.save(webpage)
    }

    @Transactional
    fun listing(webpageId: Long, tenantId: Long): WebpageEntity {
        // Webpage
        val webpage = get(webpageId, tenantId)
        logger.add("webpage_url", webpage.url)
        if (webpage.listingId != null) {
            throw ConflictException(
                error = Error(
                    code = ErrorCode.LISTING_ALREADY_CREATED,
                    data = mapOf("listing_id" to webpage.listingId.toString())
                )
            )
        }
        if (webpage.content?.trim().isNullOrEmpty()) {
            throw ConflictException(
                error = Error(ErrorCode.WEBPAGE_NO_CONTENT)
            )
        }

        // City
        val city = resolveCity(webpage)
        logger.add("city_id", city.id)
        logger.add("city_name", city.name)

        // Listing
        LOGGER.info("webpage#${webpage.id} - Creating listing for webpage: ${webpage.url}")
        val listing = createListing(webpage, city)
        logger.add("listing_id", listing.id)

        // Link Webpage and Listing
        webpage.listingId = listing.id
        return save(webpage)
    }

    private fun createListing(webpage: WebpageEntity, city: LocationEntity): ListingEntity {
        return aiListingService.create(
            request = CreateAIListingRequest(
                cityId = city.id ?: -1,
                text = webpage.content!!,
                sellerAgentUserId = webpage.website.userId,
            ),
            tenantId = webpage.tenantId
        )
    }

    private fun resolveCity(webpage: WebpageEntity): LocationEntity {
        val tenant = tenantService.get(webpage.tenantId)
        val agent = listingAgentFactory.createListingLocationExtractoryAgent(tenant.country)
        val json = agent.run(webpage.content!!)
        val result = jsonMapper.readValue(json, ListingLocationExtractorResult::class.java)
        return locationService.search(
            keyword = result.city,
            country = result.country,
            limit = 1
        ).firstOrNull()
            ?: throw NotFoundException(
                error = Error(
                    code = ErrorCode.LOCATION_NOT_FOUND,
                    message = "City '${result.city}' not found",
                    data = mapOf("city" to result.city.toString())
                )
            )
    }

    fun new(
        website: WebsiteEntity,
        url: String,
        images: List<String>,
        content: String?,
    ): WebpageEntity {
        return WebpageEntity(
            website = website,
            tenantId = website.tenantId,
            url = url,
            urlHash = http.hash(url),
            imageUrls = images,
            content = content?.ifEmpty { null },
            active = true,
        )
    }
}
