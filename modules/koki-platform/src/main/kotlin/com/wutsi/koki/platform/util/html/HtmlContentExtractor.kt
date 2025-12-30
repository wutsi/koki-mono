package com.wutsi.koki.platform.util.html

class HtmlContentExtractor(
    private val filters: List<HtmlFilter> = listOf(
        HtmlSanitizeFilter(),
        HtmlContentFilter(100),
    )
) {
    fun extract(html: String): String {
        var result = html
        for (filter in filters) {
            result = filter.filter(html)
        }
        return result
    }
}
