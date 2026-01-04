package com.wutsi.koki.platform.util.html

import org.junit.jupiter.api.Test

class HtmlContentExtractorTest : AbstractFilterTest() {
    var extractor = HtmlContentExtractor(20)

    @Test
    fun camfoot() {
        test("/html/extractor/camfoot")
    }

    private fun test(path: String) {
        validateHtml(path, object : HtmlFilter {
            override fun filter(html: String): String {
                return extractor.extract(html)
            }
        })
    }
}
