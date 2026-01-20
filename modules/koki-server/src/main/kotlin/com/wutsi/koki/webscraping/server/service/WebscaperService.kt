package com.wutsi.koki.webscraping.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.webscraping.dto.CreateWebsiteRequest
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteResponse
import com.wutsi.koki.webscraping.dto.UpdateWebsiteRequest
import com.wutsi.koki.webscraping.server.dao.WebsiteRepository
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.domain.WebsiteEntity
import jakarta.persistence.EntityManager
import org.apache.commons.codec.digest.DigestUtils
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class WebsiteService(
    private val dao: WebsiteRepository,
    private val webpageService: WebpageService,
    private val em: EntityManager,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebsiteService::class.java)
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36"
    }

    @Transactional
    fun create(request: CreateWebsiteRequest, tenantId: Long): Long {
        val baseUrlHash = generateHash(request.baseUrl)

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
        val website = dao.save(
            WebsiteEntity(
                tenantId = tenantId,
                userId = request.userId,
                baseUrl = request.baseUrl,
                baseUrlHash = baseUrlHash,
                listingUrlPrefix = request.listingUrlPrefix,
                contentSelector = request.contentSelector,
                imageSelector = request.imageSelector,
                active = request.active,
                createdAt = Date(),
            )
        )
        return website.id!!
    }

    @Transactional
    fun update(id: Long, request: UpdateWebsiteRequest, tenantId: Long) {
        val website = get(id, tenantId)

        website.listingUrlPrefix = request.listingUrlPrefix
        website.contentSelector = request.contentSelector
        website.imageSelector = request.imageSelector
        website.active = request.active

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

    fun scrape(websiteId: Long, tenantId: Long): ScrapeWebsiteResponse {
        val website = get(websiteId, tenantId)

        val doc = Jsoup.connect(website.baseUrl)
            .userAgent(USER_AGENT)
            .followRedirects(true)
            .get()

        val urls = doc.select("a[href]")
            .map { elt -> elt.absUrl("href") }
            .filter { href -> href.startsWith(website.listingUrlPrefix) }
            .distinct()

        val websites = urls.map { url ->
            try {
                scrape(url, website)
            } catch (e: Exception) {
                LOGGER.warn("Could not scrape $url", e)
                null
            }
        }

        return ScrapeWebsiteResponse(
            webpageImported = websites.size
        )
    }

    private fun scrape(url: String, website: WebsiteEntity): WebpageEntity? {
        val urlHash = generateHash(url)
        if (webpageService.getByUrlHash(urlHash, website.tenantId) != null) {
            return null
        }

        val doc = Jsoup.connect(url)
            .userAgent(USER_AGENT)
            .followRedirects(true)
            .get()

        val images = website.imageSelector?.let { selector ->
            doc.select("img")
                .map { elt -> elt.absUrl("src") }
                .filter { src -> src.startsWith(selector) }
                .distinct()
        } ?: emptyList()

        val content = website.contentSelector?.let { selector ->
            doc.select(selector)
                .joinToString("\n") { elt -> elt.text() }
        } ?: ""

        return webpageService.save(
            website = website,
            url = url,
            images = images,
            content = content,
        )
    }

    private fun generateHash(value: String): String {
        val xvalue = if (value.endsWith("/")) {
            value.substring(0, value.length - 1)
        } else {
            value
        }
        return DigestUtils.md5Hex(xvalue.lowercase().trim())
    }
}
