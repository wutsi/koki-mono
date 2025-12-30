package com.wutsi.koki.platform.util

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.util.function.Consumer

object JsoupHelper {
    fun visit(root: Element, visitor: Visitor) {
        val children = root.children()
        for (child in children) {
            visit(child, visitor)
        }
        visitor.visit(root)
    }

    fun removeComments(node: Node) {
        var i = 0
        while (i < node.childNodeSize()) {
            val child = node.childNode(i)
            if (child.nodeName() == "#comment") {
                child.remove()
            } else {
                removeComments(child)
                i++
            }
        }
    }

    fun remove(root: Element, predicate: Visitor) {
        val elts = mutableListOf<Element>()
        filter(root, elts, predicate)
        elts.forEach(Consumer { it: Element? -> it!!.remove() })
    }

    fun filter(root: Element, result: MutableCollection<Element>, predicate: Visitor) {
        if (predicate.visit(root)) {
            result.add(root)
        }

        val children = root.children()
        for (child in children) {
            filter(child, result, predicate)
        }
    }

    fun findFirst(root: Element, predicate: Visitor): Element? {
        if (predicate.visit(root)) {
            return root
        }

        val children = root.children()
        for (child in children) {
            val elt = findFirst(child, predicate)
            if (elt != null) {
                return elt
            }
        }
        return null
    }

    fun select(doc: Document, cssSelector: String): String? {
        val elts = doc.select(cssSelector)
        return if (elts.isEmpty()) null else elts.get(0).text()
    }

    interface Visitor {
        fun visit(obj: Element): Boolean
    }
}
