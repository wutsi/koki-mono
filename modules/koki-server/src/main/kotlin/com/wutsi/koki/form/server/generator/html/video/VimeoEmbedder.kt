package com.wutsi.koki.form.server.generator.html.video

import java.util.regex.Pattern

class YouTubeEmbedder() : VideoEmbedder {
    private val pattern =
        Pattern.compile(
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*",
            Pattern.CASE_INSENSITIVE,
        )

    override fun embedUrl(url: String): String? {
        val matcher = pattern.matcher(url)
        val id = if (matcher.find()) matcher.group() else null
        return id?.let { "https://www.youtube.com/embed/$id" }
    }
}
