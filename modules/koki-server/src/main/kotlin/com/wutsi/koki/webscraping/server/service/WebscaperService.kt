package com.wutsi.koki.webscraping.server.service

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.wutsi.koki.platform.util.html.HtmlSanitizeFilter
import com.wutsi.koki.webscraping.dto.ScrapeWebsiteRequest
import com.wutsi.koki.webscraping.server.domain.WebpageEntity
import com.wutsi.koki.webscraping.server.domain.WebsiteEntity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WebscaperService(
    private val webpageService: WebpageService,
    private val http: Http,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebscaperService::class.java)
        private val MULTIPLE_CR_REGEX = Regex("\\n{2,}")
        private val MULTIPLE_SP_REGEX = Regex("\\s{2,}")
        private val CR = "\n"
        private val SP = " "
    }

    private val sanitizerFilter = HtmlSanitizeFilter()

    fun scrape(website: WebsiteEntity, request: ScrapeWebsiteRequest): List<WebpageEntity> {
        val homeUrls = website.homeUrls.ifEmpty { listOf(website.baseUrl) }
        val result = mutableListOf<WebpageEntity>()
        val listingUrlPrefix = if (website.listingUrlPrefix.startsWith(website.baseUrl)) {
            website.listingUrlPrefix
        } else {
            website.baseUrl.trimEnd('/') + "/" + website.listingUrlPrefix.trimStart('/')
        }

        homeUrls.forEach { homeUrl ->
            try {
                LOGGER.info("Scraping home URL: $homeUrl")
                val doc = get(homeUrl, website.baseUrl)
                val urls = doc.select("a[href]")
                    .map { elt -> elt.absUrl("href") }
                    .filter { href -> href.startsWith(listingUrlPrefix, ignoreCase = true) }
                    .distinct()
                LOGGER.info("${urls.size} URLs with prefix $listingUrlPrefix")

                urls.forEach { url ->
                    if (result.size < request.limit) {
                        try {
                            LOGGER.info("Scraping webpage: $url")
                            val webpage = scrape(url, website, request)
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

    private fun scrape(url: String, website: WebsiteEntity, request: ScrapeWebsiteRequest): WebpageEntity? {
        val doc = get(url, website.baseUrl)
        val urlHash = http.hash(url)
        var webpage = webpageService.getByUrlHash(urlHash, website.tenantId)
        if (webpage == null) {
            webpage = webpageService.new(
                website = website,
                url = url,
                images = extractImages(doc, website),
                content = extractContent(doc, website),
            )
        } else {
            webpage.imageUrls = extractImages(doc, website)
            webpage.content = extractContent(doc, website)
        }
        return if (request.testMode) {
            webpage
        } else {
            webpageService.save(webpage)
        }
    }

    private fun extractImages(doc: Document, website: WebsiteEntity): List<String> {
        if (website.imageSelector.isNullOrEmpty()) {
            return emptyList()
        }

        return doc.select(website.imageSelector!!)
            .map { elt -> elt.absUrl("src") }
            .distinct()
    }

    private fun extractContent(doc: Document, website: WebsiteEntity): String? {
        if (website.contentSelector.isNullOrEmpty()) {
            return null
        } else {
            return doc.select(website.contentSelector!!)
                .joinToString("\n") { elt -> html2markdown(elt) }
        }
    }

    private fun html2markdown(elt: Element): String {
        val content = sanitizerFilter.filter(elt.html())
        return FlexmarkHtmlConverter.builder().build().convert(content)
    }

    private fun toText(element: Element): String {
        val sb = StringBuilder()

        for (node in element.childNodes()) {
            when (node) {
                is TextNode -> {
                    sb.append(node.text().trim())
                }

                is Element -> {
                    val tag = node.tagName()
                    when {
                        tag == "br" -> sb.append(CR)
                        tag == "hr" -> sb.append("$CR-----------$CR")
                        tag == "li" -> sb.append("- ")
                        !node.isBlock -> sb.append(SP)
                    }

                    // Append text of the child element recursively
                    sb.append(toText(node))

                    // Add carriage returns for specific tags

                    when {
                        node.isBlock -> sb.append(CR)
                        else -> {}
                    }
                }
            }
        }
        return sb.toString().replace(MULTIPLE_CR_REGEX, CR)
            .replace(MULTIPLE_SP_REGEX, SP)
    }

    private fun get(url: String, baseUrl: String): Document {
        val html = http.get(url)
        return Jsoup.parse(html, baseUrl)
    }
}
