package com.wutsi.koki.platform.util.html

import org.junit.jupiter.api.Test

class HtmlContentFilterTest : AbstractFilterTest() {
    private var filter = HtmlContentFilter(20)

    @Test
    @Throws(Exception::class)
    fun simple() {
        validateText("/html/content/simple", filter)
    }

    @Test
    @Throws(Exception::class)
    fun style() {
        validateText("/html/content/style", filter)
    }

    @Test
    @Throws(Exception::class)
    fun quote() {
        validateText("/html/content/quote", filter)
    }

    @Test
    @Throws(Exception::class)
    fun figure() {
        validateText("/html/content/figure", filter)
    }

    @Test
    @Throws(Exception::class)
    fun jounalducameroon() {
        filter = HtmlContentFilter(100)
        validateText("/html/content/journal_du_cameroon", filter)
    }

    @Test
    @Throws(Exception::class)
    fun investiraucameroun() {
        filter = HtmlContentFilter(100)
        validateText("/html/content/investir_au_cameroun", filter)
    }

    @Test
    @Throws(Exception::class)
    fun camfoot() {
        filter = HtmlContentFilter(100)
        validateText("/html/content/camfoot", filter)
    }

    @Test
    @Throws(Exception::class)
    fun jewanda() {
        filter = HtmlContentFilter(100)
        validateText("/html/content/jewanda", filter)
    }

    @Test
    @Throws(Exception::class)
    fun arolketch() {
        filter = HtmlContentFilter(100)
        validateText("/html/content/arol_ketch", filter)
    }

    @Test
    @Throws(Exception::class)
    fun people237() {
        filter = HtmlContentFilter(100)
        validateText("/html/content/people237", filter)
    }
}
