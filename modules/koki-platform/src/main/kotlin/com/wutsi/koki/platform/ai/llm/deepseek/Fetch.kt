package com.wutsi.koki.platform.ai.llm.deepseek

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.wutsi.koki.platform.util.html.HtmlContentExtractor
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.net.ConnectException
import java.net.URL

/**
 * Fetches the content of a web page and converts it to markdown.
 */
class Fetch {
    companion object {
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36"
    }

    private val extractor = HtmlContentExtractor()

    fun fetch(url: String): String {
        try {
            if (isPdf(url)) {
                return fetchPdf(url)
            } else {
                return fetchHtml(url)
            }
        } catch (ex: ConnectException) {
            return "Failed to connect to $url"
        } catch (ex: HttpStatusException) {
            if (ex.statusCode == 404) {
                return "Failed to get the content from $url - The page doesn't exist or is inaccessible"
            } else {
                return "Failed to get the content from $url - HTTP status code ${ex.statusCode}"
            }
        } catch (ex: Exception) {
            return "Failed to get the content from $url -  ${ex.message}"
        }
    }

    private fun fetchHtml(url: String): String {
        val html = Jsoup.connect(url)
            .userAgent(USER_AGENT)
            .followRedirects(true)
            .get()
            .html()
        return toMarkdown(html)
    }

    private fun fetchPdf(url: String): String {
        val content = URL(url).readBytes()
        val doc = Loader.loadPDF(content)
        val stripper = PDFTextStripper()
        stripper.startPage = 1
        stripper.endPage = doc.numberOfPages
        return stripper.getText(doc)
    }

    private fun toMarkdown(html: String): String {
        val content = extractor.extract(html)
        if (isEmpty(content)) {
            return "No content found"
        } else {
            return FlexmarkHtmlConverter.builder().build().convert(content)
        }
    }

    private fun isEmpty(html: String): Boolean {
        return Jsoup.parse(html).body().text().trim().isEmpty()
    }

    private fun isPdf(url: String): Boolean {
        return url.lowercase().endsWith(".pdf")
    }
}
