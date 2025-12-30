package com.wutsi.koki.platform.ai.llm.deepseek

import org.jsoup.Jsoup
import java.net.URLEncoder

class DuckDuckGoWebsearch : Websearch {
    companion object {
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36"
        const val URL_PREFIX = "https://duckduckgo.com/html/?q="
    }

    override fun search(query: String): String {
        val url = URL_PREFIX + URLEncoder.encode(query, "UTF-8")
        try {
            val doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .followRedirects(true)
                .get()

            val sb = StringBuffer()
            val results = doc.select(".result")
            var i = 0
            results.forEach { result ->
                val title = result.select(".result__title").text()
                val link = result.select(".result__a").attr("abs:href")
                val snippet = result.select(".result__snippet").text()

                sb.append("- Result #${++i}\n")
                sb.append("  - Title: ").append(title).append("\n")
                sb.append("  - Link: ").append(link).append("\n")
                sb.append("  - Snippet: ").append(snippet).append("\n")
            }
            return sb.toString()
        } catch (e: Exception) {
            return "Failed to perform web search for query '$query' - ${e.message}"
        }
    }
}
