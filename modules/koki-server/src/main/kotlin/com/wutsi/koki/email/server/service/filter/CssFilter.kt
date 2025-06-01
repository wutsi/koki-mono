package com.wutsi.koki.email.server.service.filter

import com.wutsi.koki.email.server.service.EmailFilter
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Entities.EscapeMode.extended
import org.springframework.stereotype.Service

@Service
class CssFilter : EmailFilter {
    companion object {
        private val STYLES = mapOf(
            ".social" to """
                text-decoration: none
            """.trimIndent(),

            ".btn-primary" to """
                border-radius: 16px;
                display: inline-block;
                font-weight: 400;
                color: #FFFFFF;
                background-color: #1D7EDF;
                text-align: center;
                vertical-align: middle;
                border: 1px solid transparent;
                padding: .375rem .75rem;
                font-size: 1rem;
                line-height: 1.5;
                text-decoration: none;
            """.trimIndent(),

            ".btn-success" to """
                border-radius: 16px;
                display: inline-block;
                font-weight: 400;
                color: #FFFFFF;
                background-color: #4CAF50;
                text-align: center;
                vertical-align: middle;
                border: 1px solid transparent;
                padding: .375rem .75rem;
                font-size: 1rem;
                line-height: 1.5;
                text-decoration: none;
            """.trimIndent(),

            ".btn-secondary" to """
                border-radius: 16px;
                display: inline-block;
                font-weight: 400;
                color: gray;
                background-color: #e4edf7;
                text-align: center;
                vertical-align: middle;
                border: 1px solid lightgray;
                padding: .375rem .75rem;
                line-height: 1.5;
                text-decoration: none;
            """.trimIndent(),

            ".text-center" to """
                text-align: center;
            """.trimIndent(),

            ".text-larger" to """
                font-size: larger;
            """.trimIndent(),

            ".text-smaller" to """
                font-size: smaller;
            """.trimIndent(),

            ".text-small" to """
                font-size: small;
            """.trimIndent(),

            ".text-x-small" to """
                font-size: x-small;
            """.trimIndent(),

            ".no-margin" to """
                margin: 0
            """.trimIndent(),

            ".no-padding" to """
                padding: 0
            """.trimIndent(),

            ".no-text-decoration" to """
                text-decoration: none
            """.trimIndent(),

            ".border" to """
                border: 1px solid lightgray;
            """.trimIndent(),

            ".border-rounded" to """
                border-radius: 16px;
            """.trimIndent(),

            ".border-rounded-top" to """
                border-radius: 16px 16px 0 0;
            """.trimIndent(),

            ".padding-small" to """
                padding: 8px;
            """.trimIndent(),

            ".padding" to """
                padding: 16px;
            """.trimIndent(),

            ".padding-2x" to """
                padding: 32px;
            """.trimIndent(),

            ".padding-top" to """
                padding-top: 16px;
            """.trimIndent(),

            ".padding-top-small" to """
                padding-top: 8px;
            """.trimIndent(),

            ".padding-bottom" to """
                padding-bottom: 16px;
            """.trimIndent(),

            ".padding-left-small" to """
                padding-left: 8px;
            """.trimIndent(),

            ".border-top" to """
                border-top: 1px solid lightgray;
            """.trimIndent(),

            ".border-bottom" to """
                border-bottom: 1px solid lightgray;
            """.trimIndent(),

            ".border-left" to """
                border-left: 1px solid lightgray;
            """.trimIndent(),

            ".border-right" to """
                border-right: 1px solid lightgray;
            """.trimIndent(),

            ".margin" to """
                margin: 16px;
            """.trimIndent(),

            ".margin-bottom" to """
                margin-bottom: 16px;
            """.trimIndent(),

            ".margin-bottom-small" to """
                margin-bottom: 8px;
            """.trimIndent(),

            ".margin-left" to """
                margin-left: 16px;
            """.trimIndent(),

            ".margin-top" to """
                margin-top: 16px;
            """.trimIndent(),

            ".margin-top-small" to """
                margin-top: 8px;
            """.trimIndent(),

            ".margin-top-none" to """
                margin-top: 0px;
            """.trimIndent(),

            ".margin-right" to """
                margin-right: 16px;
            """.trimIndent(),

            ".rounded" to """
                border-radius: 5px
            """.trimIndent(),

            ".box-highlight" to """
                background: #1D7EDF;
                border: 1px solid #1D7EDF;
            """.trimIndent(),

            ".box-highlight-light" to """
                background: #e4edf7;
                border: 1px solid #1D7EDF;
            """.trimIndent(),

            ".box-highlight-gray" to """
                background: lightgray;
                border: 1px solid lightgray;
            """.trimIndent(),

            ".box-highlight-white" to """
                background: white;
                border: 1px solid lightgray;
            """.trimIndent(),

            ".highlight" to """
                color: #1D7EDF
            """.trimIndent(),

            ".success" to """
                color: #4CAF50
            """.trimIndent(),
        )
    }

    override fun filter(html: String, tenantId: Long): String {
        val doc = Jsoup.parse(html)
        val elts = doc.select("[class]")
        if (elts.isEmpty()) {
            return html
        }

        elts.forEach { elt ->
            filter(elt)
        }
        doc
            .outputSettings()
            .charset("ASCII")
            .escapeMode(extended)
            .indentAmount(2)
            .prettyPrint(true)
            .outline(true)
        return doc.html()
    }

    private fun filter(elt: Element) {
        STYLES.keys.forEach { selector ->
            val clazz = selector.substring(1)
            if (elt.hasClass(clazz)) {
                STYLES[selector]
                    ?.replace("\n", "")
                    ?.let { style ->
                        if (elt.hasAttr("style")) {
                            elt.attr("style", elt.attr("style") + ";$style")
                        } else {
                            elt.attr("style", style)
                        }
                    }
            }
        }
    }
}
