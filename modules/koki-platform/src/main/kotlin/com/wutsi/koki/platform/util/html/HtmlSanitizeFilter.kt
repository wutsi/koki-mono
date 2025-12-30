package com.wutsi.koki.platform.util.html

import com.wutsi.koki.platform.util.JsoupHelper
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.Locale

class HtmlSanitizeFilter : HtmlFilter {
    companion object {
        val ID_CSS_BLACKLIST: MutableList<String?> = mutableListOf<String?>(
            "footer",
            "comments",
            "menu-ay-side-menu-mine",
            "mashsb-container",
            "top-nav",
            "related_posts",
            "share-post",
            "navbar",
            "nav",
            "addthis_tool",
            "embedly-card",
            "sidebar",
            "rrssb-buttons", // See https://github.com/AdamPS/rrssb-plus
            "the_champ_sharing_container", // https://github.com/wp-plugins/super-socializer
            "a2a_kit", // https://www.addtoany.com/

            "jeg_share_top_container",
            "jeg_share_bottom_container",
            "jeg_post_tags",
            "jp-relatedposts",
            "truncate-read-more",
            "jnews_author_box_container",
            "jnews_related_post_container",
            "jnews_prev_next_container",
            "jnews_inline_related_post_wrapper",
            "ads-wrapper",

            "td-post-sharing"
        )

        val TAG_BLACKLIST: MutableList<String?> = mutableListOf<String?>(
            "head",
            "style",
            "script",
            "nav",
            "iframe",
            "noscript",
            "header",
            "footer",
            "aside",
            "form"
        )
    }

    // -- TextFilter overrides
    public override fun filter(html: String): String {
        val doc = Jsoup.parse(html)

        JsoupHelper.removeComments(doc.body())

        JsoupHelper.remove(doc, object : JsoupHelper.Visitor {
            override fun visit(elt: Element): Boolean {
                return reject(elt)
            }
        })

        JsoupHelper.visit(doc, object : JsoupHelper.Visitor {
            override fun visit(elt: Element): Boolean {
                return empty(elt)
            }
        })

        JsoupHelper.visit(doc, object : JsoupHelper.Visitor {
            override fun visit(elt: Element): Boolean {
                return cleanup(elt)
            }
        })
        return doc.html()
    }

    private fun cleanup(elt: Element): Boolean {
        elt.removeAttr("id")
        elt.removeAttr("class")
        elt.removeAttr("style")
        elt.removeAttr("onclick")

        return true
    }

    private fun reject(elt: Element): Boolean {
        return TAG_BLACKLIST.contains(elt.tagName()) ||
            isSocialLink(elt) ||
            isTagLink(elt) ||
            isBlacklistedClassOrId(elt)
        //                || isMenu(elt)
    }

    private fun isSocialLink(elt: Element): Boolean {
        if ("a" != elt.tagName()) {
            return false
        }

        val href = elt.attr("href")
        return href.contains("twitter.com/intent/tweet") ||
            href.contains("twitter.com/share") ||
            href.contains("facebook.com/share.php") ||
            href.contains("facebook.com/sharer.php") ||
            href.contains("plus.google.com/share") ||
            href.contains("linkedin.com/shareArticle") ||
            href.contains("linkedin.com/cws/share") ||
            href.contains("pinterest.com/pin/create/button")
    }

    private fun isImage(elt: Element): Boolean {
        val tagName = elt.tagName()
        return "figure".equals(tagName, ignoreCase = true) || "img".equals(tagName, ignoreCase = true)
    }

    private fun isTagLink(elt: Element): Boolean {
        if (!isLink(elt)) {
            return false
        }
        return elt.hasClass("tag") || hasRel(elt, "tag")
    }

    private fun hasRel(elt: Element, value: String?): Boolean {
        val rel = elt.attr("rel")
        if (rel.isEmpty()) {
            return false
        }

        val parts = rel.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (part in parts) {
            if (part.equals(value, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun isLink(elt: Element): Boolean {
        return "a" == elt.tagName()
    }

    private fun isBlacklistedClassOrId(elt: Element): Boolean {
        for (clazz in elt.classNames()) {
            if (ID_CSS_BLACKLIST.contains(clazz.lowercase(Locale.getDefault()))) {
                return true
            }
        }
        return ID_CSS_BLACKLIST.contains(elt.attr("id").lowercase(Locale.getDefault()))
    }

    private fun empty(elt: Element): Boolean {
        if (elt.tag().isBlock() && !elt.hasText() && elt.children().isEmpty()) {
            elt.remove()
            return true
        }
        return false
    }
}
