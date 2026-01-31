package com.wutsi.koki.bot.server.service

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.wutsi.koki.bot.dto.ScrapeWebsiteRequest
import com.wutsi.koki.bot.server.dao.WebpageRepository
import com.wutsi.koki.bot.server.domain.WebpageEntity
import com.wutsi.koki.bot.server.domain.WebsiteEntity
import com.wutsi.koki.platform.util.html.HtmlSanitizeFilter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.AntPathMatcher

@Service
class WebscraperService(
    private val webpageDao: WebpageRepository,
    private val http: Http
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebscraperService::class.java)
        private val MULTIPLE_CR_REGEX = Regex("\\n{2,}")
        private val MULTIPLE_SP_REGEX = Regex("\\s{2,}")
        private val CR = "\n"
        private val SP = " "
    }

    private val sanitizerFilter = HtmlSanitizeFilter()
    private val antMatcher = AntPathMatcher()

    fun scrape(site: WebsiteEntity, request: ScrapeWebsiteRequest): List<WebpageEntity> {
        val homeUrls = site.homeUrls.ifEmpty { listOf(site.baseUrl) }
        val result = mutableListOf<WebpageEntity>()
        val listingUrlPrefixes = site.listingUrlPrefixes.map { prefix ->
            if (prefix.startsWith("http://") || prefix.startsWith("https://")) {
                prefix
            } else {
                site.baseUrl.trimEnd('/') + "/" + prefix.trimStart('/')
            }
        }

        homeUrls.forEach { homeUrl ->
            try {
                LOGGER.info("Scraping: $homeUrl")
                val doc = document(homeUrl, site.baseUrl)
                val urls = doc.select("a[href]")
                    .map { elt -> elt.absUrl("href") }
                    .filter { url -> hasPrefix(url, listingUrlPrefixes) }
                    .distinct()

                urls.forEach { url ->
                    if (result.size < request.limit) {
                        try {
                            val webpage = scrape(url, site, request)
                            if (webpage != null) {
                                result.add(webpage)
                            }
                        } catch (e: Exception) {
                            LOGGER.warn("Could not scrape $url", e)
                        }
                    }
                }
            } catch (e: Exception) {
                LOGGER.warn("Could not scrape $homeUrl", e)
            }
        }
        return result
    }

    private fun hasPrefix(url: String, listingUrlPrefixes: List<String>): Boolean {
        listingUrlPrefixes.forEach { prefix ->
            if (prefix.contains("*")) {
                if (antMatcher.match(prefix, url)) {
                    return true
                }
            } else {
                if (url.startsWith(prefix, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    private fun scrape(url: String, site: WebsiteEntity, request: ScrapeWebsiteRequest): WebpageEntity? {
        var page = webpageDao.findByUrl(url, site)
        if (page != null && !request.overwrite) {
            return null
        }

        LOGGER.info("Scraping webpage: $url")
        val doc = document(url, site.baseUrl)
        if (page == null) {
            page = WebpageEntity(
                url = url,
                imageUrls = extractImages(doc, site),
                content = extractContent(doc, site),
            )
        } else {
            page.imageUrls = extractImages(doc, site)
            page.content = extractContent(doc, site)
        }

        return webpageDao.save(page, site)
    }

    private fun extractImages(doc: Document, website: WebsiteEntity): List<String> {
        if (website.imageSelector.isNullOrEmpty()) {
            return emptyList()
        }

        return doc.select(website.imageSelector!!)
            .map { elt -> elt.absUrl("src") }
            .distinct()
    }

    private fun extractContent(doc: Document, website: WebsiteEntity): String {
        if (website.contentSelector.isNullOrEmpty()) {
            return ""
        } else {
            return doc.select(website.contentSelector!!)
                .joinToString("\n") { elt -> html2markdown(elt) }
        }
    }

    private fun html2markdown(elt: Element): String {
        val content = sanitizerFilter.filter(elt.html())
        return FlexmarkHtmlConverter.builder().build().convert(content)
    }

    private fun document(url: String, baseUrl: String): Document {
        val html = http.html(url)
        return Jsoup.parse(html, baseUrl)
    }
}
