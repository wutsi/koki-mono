package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.server.generator.html.video.VimeoEmbedder
import kotlin.test.Test
import kotlin.test.assertEquals

class VimoeEmbedderTest {
    val embedder = VimeoEmbedder()

    @Test
    fun url() {
        assertEquals(
            "https://player.vimeo.com/video/1264718256809656320",
            embedder.embedUrl("https://www.vimeo.com/1264718256809656320")
        )
    }
}
