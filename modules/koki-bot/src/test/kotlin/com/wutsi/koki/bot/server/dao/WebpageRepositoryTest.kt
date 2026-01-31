package com.wutsi.koki.bot.server.dao

import com.wutsi.koki.bot.server.domain.WebpageEntity
import com.wutsi.koki.bot.server.domain.WebsiteEntity
import com.wutsi.koki.bot.server.service.Http
import com.wutsi.koki.platform.storage.local.LocalStorageService
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper
import java.io.ByteArrayOutputStream
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class WebpageRepositoryTest {
    private val directory = System.getProperty("user.home") + "/__wutsi"
    private val baseUrl = "http://localhost:1111/storage"
    private val storage = LocalStorageService(directory, baseUrl)
    private val jsonMapper = JsonMapper()
    private val dao = WebpageRepository(
        jsonMapper = jsonMapper,
        storage = storage,
        http = Http(),
    )

    private val site = WebsiteEntity(
        name = "example.com",
        baseUrl = "https://example.com",
    )

    @Test
    fun save() {
        // WHEN
        val page = WebpageEntity(
            url = "https://example.com/page1",
            content = "<html>...</html>",
            imageUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
        )
        dao.save(page, site)

        // THEN
        val output = ByteArrayOutputStream()
        val url = dao.toContentUrl(page, site)
        assertNotNull(url)
        storage.get(url, output)

        assertEquals(
            jsonMapper.writeValueAsString(page),
            IOUtils.toString(output.toByteArray(), "UTF-8")
        )
    }

    @Test
    fun findByUrl() {
        // GIVEN
        val page = WebpageEntity(
            url = "https://example.com/find-by-url",
            content = "<html>...</html>",
            imageUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
        )
        dao.save(page, site)

        // WHEN
        val result = dao.findByUrl(page.url, site)

        // THEN
        assertEquals(page, result)
    }

    @Test
    fun `findByUrl - not found`() {
        // WHEN
        val result = dao.findByUrl("$baseUrl/not-found", site)

        // THEN
        assertNull(result)
    }
}
