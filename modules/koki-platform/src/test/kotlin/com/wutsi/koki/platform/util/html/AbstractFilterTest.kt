package com.wutsi.koki.platform.util.html

import org.apache.commons.io.IOUtils
import org.assertj.core.api.Assertions
import org.jsoup.Jsoup

abstract class AbstractFilterTest {
    @Throws(Exception::class)
    protected fun validateText(name: String?, filter: HtmlFilter) {
        // Given
        val html = IOUtils.toString(javaClass.getResourceAsStream(name + ".html"))
        val expected = IOUtils.toString(javaClass.getResourceAsStream(name + suffix()))

        // When
        val result: String = filter.filter(html)!!
        println(javaClass.toString() + " - " + name)
        println(result)

        // Then
        val resultText = Jsoup.parse(result).text()
        val expectedText = Jsoup.parse(expected).text()
        Assertions.assertThat(resultText).isEqualTo(expectedText)
    }

    @Throws(Exception::class)
    protected fun validateHtml(name: String?, filter: HtmlFilter) {
        // Given
        val html = IOUtils.toString(javaClass.getResourceAsStream(name + ".html"))
        val expected = IOUtils.toString(javaClass.getResourceAsStream(name + suffix()))

        // When
        val result: String = filter.filter(html)!!
        println(javaClass.toString() + " - " + name)
        println(result)

        // Then
        val resultHtml = Jsoup.parse(result).body().html().trim { it <= ' ' }
        val expectedHtml = Jsoup.parse(expected).body().html().trim { it <= ' ' }
        Assertions.assertThat(resultHtml).isEqualTo(expectedHtml)
    }

    protected fun suffix(): String {
        return "_output.html"
    }
}
