package com.wutsi.koki.form.server.generator.html.tag

import com.wutsi.koki.form.server.generator.html.video.YouTubeEmbedder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class YouTubeEmbedderTest {
    val embedder = YouTubeEmbedder()

    @Test
    fun url() {
        assertEquals(
            "https://www.youtube.com/embed/l9lLQLckJn4",
            embedder.embedUrl("https://www.youtube.com/watch?v=l9lLQLckJn4")
        )
    }

    @Test
    fun `url with query params`() {
        assertEquals(
            "https://www.youtube.com/embed/XJwQ4UWUOmo",
            embedder.embedUrl("https://www.youtube.com/watch?v=XJwQ4UWUOmo&list=WL&index=1")
        )
    }

    @Test
    fun `short url`() {
        assertEquals(
            "https://www.youtube.com/embed/XJwQ4UWUOmo",
            embedder.embedUrl("https://youtu.be/XJwQ4UWUOmo?si=54Na32BWT-Q6PqUZ")
        )
    }

    @Test
    fun `bad url`() {
        assertNull(
            embedder.embedUrl("https://www.google.com")
        )
    }
}
