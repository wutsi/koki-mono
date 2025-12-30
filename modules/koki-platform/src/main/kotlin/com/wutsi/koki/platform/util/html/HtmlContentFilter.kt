package com.wutsi.koki.platform.util.html

import com.wutsi.koki.platform.util.JsoupHelper
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.stream.Collectors
import kotlin.jvm.optionals.getOrNull

/**
 * Extract main content.
 * Implementation of https://rodricios.github.io/eatiht/#the-original-algorithm
 */
class HtmlContentFilter(private val blocMinLen: Int = 20) : HtmlFilter {
    companion object {
        val INLINE_TAGS: MutableList<String?> = mutableListOf<String?>(
            "i",
            "b",
            "font",
            "em",
            "small",
            "mark",
            "del",
            "ins",
            "q",
            "cite",
            "sub",
            "sup",
            "strong",
            "span",
            "a"
        )
    }

    override fun filter(html: String): String {
        val parts = select(html)
        val histogram = partition(parts)
        val max = argmax(histogram)
        val xparts = merge(max, parts)
        return toHtml(xparts)
    }

    // -- Private
    private fun select(html: String): MutableList<Element> {
        val body = Jsoup.parse(html).body()
        val blocs = mutableListOf<Element>()

        val visitor = object : JsoupHelper.Visitor {
            override fun visit(elt: Element): Boolean {
                return accept(body, elt)
            }
        }
        JsoupHelper.filter(body, blocs, visitor)

        return blocs.stream()
            .map<Element> { it: Element -> it!!.parent() }
            .distinct()
            .collect(Collectors.toList())
    }

    private fun partition(parts: MutableList<Element>): MutableMap<Element, Int> {
        val result = mutableMapOf<Element, Int>()
        for (part in parts) {
            val value = part.children().stream()
                .filter { it: Element -> it.text().length > blocMinLen }
                .map { it: Element -> sentenceCount(it.text()) }
                .reduce { a, b -> a + b }
                .getOrNull()
            result.put(part, value ?: 0)
        }

        return result
    }

    private fun argmax(parts: MutableMap<Element, Int>): Element? {
        val sorted = parts.keys.stream()
            .sorted { u: Element, v: Element -> (parts.get(u)!! - parts.get(v)!!).toInt() }
            .collect(Collectors.toList())

        return if (sorted.isEmpty()) null else sorted.get(sorted.size - 1)
    }

    private fun merge(max: Element?, parts: MutableList<Element>): MutableList<Element> {
        val all: MutableList<Element> = mutableListOf()
        if (max != null) {
            for (part in parts) {
                if (inPath(max, part)) {
                    all.add(part)
                }
            }
        }
        return all
    }

    private fun toHtml(nodes: MutableList<Element>): String {
        val doc = Jsoup.parse("")
        var elts: MutableList<Element>? = nodes

        if (nodes.size == 1) {
            val node = nodes.get(0)
            if (node.children().size > 0) {
                elts = node.children()
            }
        }

        elts!!.stream().forEach { it: Element -> doc.body().appendChild(it!!) }
        return doc.body().html()
    }

    private fun sentenceCount(str: String): Int {
        val parts: Array<String?> =
            str.split("(?<=[a-z])\\.\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return parts.size
    }

    private fun accept(root: Element, elt: Element): Boolean {
        return elt !== root &&
            isLeaf(elt) &&
            !INLINE_TAGS.contains(elt.tagName()) &&
            elt.text().trim { it <= ' ' }.length > blocMinLen
    }

    private fun isLeaf(elt: Element): Boolean {
        for (child in elt.children()) {
            if (!INLINE_TAGS.contains(child.tagName())) {
                return false
            }
        }
        return true
    }

    private fun inPath(max: Element, part: Element): Boolean {
        val visitor = object : JsoupHelper.Visitor {
            override fun visit(elt: Element): Boolean {
                return elt === max
            }
        }

        return JsoupHelper.findFirst(part, visitor) != null
    }
}
