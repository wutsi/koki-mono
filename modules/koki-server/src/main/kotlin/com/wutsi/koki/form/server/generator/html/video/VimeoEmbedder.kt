package com.wutsi.koki.form.server.generator.html.video

import java.util.regex.Pattern

class VimeoEmbedder() : VideoEmbedder {
    private val pattern = Pattern.compile(
        "[http|https]+:\\/\\/(?:www\\.|)vimeo\\.com\\/([a-zA-Z0-9_\\-]+)(&.+)?",
        Pattern.CASE_INSENSITIVE,
    )

    override fun embedUrl(url: String): String? {
        val matcher = pattern.matcher(url)
        val id = if (matcher.find()) matcher.group(1) else null
        return id?.let { "https://player.vimeo.com/video/$id" }
    }
}
