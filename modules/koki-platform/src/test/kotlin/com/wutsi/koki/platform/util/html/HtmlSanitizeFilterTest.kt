package com.wutsi.koki.platform.util.html

import org.junit.jupiter.api.Test

class HtmlSanitizeFilterTest : AbstractFilterTest() {
    private val filter = HtmlSanitizeFilter()

    @Test
    @Throws(Exception::class)
    fun document() {
        validateHtml("/html/sanitizer/document", filter)
    }

    @Test
    @Throws(Exception::class)
    fun social() {
        validateHtml("/html/sanitizer/social", filter)
    }

    @Test
    @Throws(Exception::class)
    fun menu() {
        validateHtml("/html/sanitizer/menu", filter)
    }

    @Test
    @Throws(Exception::class)
    fun investiraucameroun() {
        validateText("/html/sanitizer/investir_au_cameroun", filter)
    }

    @Test
    @Throws(Exception::class)
    fun camfoot() {
        validateText("/html/sanitizer/camfoot", filter)
    }

    @Test
    @Throws(Exception::class)
    fun jewanda() {
        validateText("/html/sanitizer/jewanda", filter)
    }

    @Test
    @Throws(Exception::class)
    fun arolketch() {
        validateText("/html/sanitizer/arol_ketch", filter)
    }

    @Test
    @Throws(Exception::class)
    fun people237() {
        validateText("/html/sanitizer/people237", filter)
    }
}
