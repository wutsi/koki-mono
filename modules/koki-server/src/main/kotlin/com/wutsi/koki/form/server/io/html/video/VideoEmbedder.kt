package com.wutsi.koki.form.server.generator.html.video

interface VideoEmbedder {
    fun embedUrl(url: String): String?
}
