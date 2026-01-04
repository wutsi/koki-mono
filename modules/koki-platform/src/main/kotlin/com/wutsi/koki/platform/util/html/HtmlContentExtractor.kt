package com.wutsi.koki.platform.util.html

class HtmlContentExtractor(val minBlockLen: Int = 20) {
    private val filters: List<HtmlFilter> = listOf(
        HtmlSanitizeFilter(),
        HtmlContentFilter(minBlockLen),
    )

    fun extract(html: String): String {
        var result = html
        for (filter in filters) {
            result = filter.filter(result)
        }
        return result
    }
}
