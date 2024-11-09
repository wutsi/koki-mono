package com.wutsi.koki.form.server.generator.html

import com.wutsi.koki.form.dto.FormElement
import com.wutsi.koki.form.server.generator.html.video.VideoEmbedder
import com.wutsi.koki.form.server.generator.html.video.VimeoEmbedder
import com.wutsi.koki.form.server.generator.html.video.YouTubeEmbedder
import java.io.StringWriter

class HTMLVideoWriter(
    private val embedders: List<VideoEmbedder> = listOf(
        YouTubeEmbedder(),
        VimeoEmbedder(),
    )
) : AbstractHTMLElementWriter() {
    override fun doWrite(element: FormElement, context: Context, writer: StringWriter, readOnly: Boolean) {
        if (element.url != null) {
            val xurl = embedders
                .mapNotNull { embedder -> embedder.embedUrl(element.url!!) }
                .firstOrNull()
            if (xurl != null) {
                writer.write("<IFRAME src='$xurl'></IFRAME>\n")
            }
        }
    }
}
