package com.wutsi.koki.bot.server.dao

import com.wutsi.koki.bot.server.domain.WebpageEntity
import com.wutsi.koki.bot.server.domain.WebsiteEntity
import com.wutsi.koki.bot.server.service.Http
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageVisitor
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL

@Service
class WebpageRepository(
    private val jsonMapper: JsonMapper,
    private val storage: StorageService,
    private val http: Http,
) {
    fun save(page: WebpageEntity, site: WebsiteEntity): WebpageEntity {
        val json = jsonMapper.writeValueAsString(page)
        storage.store(
            path = toPath(page.url, site),
            content = ByteArrayInputStream(json.toByteArray()),
            contentType = "application/json",
            contentLength = json.length.toLong(),
        )
        return page
    }

    fun findByUrl(url: String, site: WebsiteEntity): WebpageEntity? {
        val path = toPath(url, site)
        var url: URL? = null
        storage.visit(
            path,
            object : StorageVisitor {
                override fun visit(u: URL) {
                    url = u
                }
            }
        )
        if (url != null) {
            val output = ByteArrayOutputStream()
            storage.get(url, output)
            return jsonMapper.readValue(ByteArrayInputStream(output.toByteArray()), WebpageEntity::class.java)
        } else {
            return null
        }
    }

    fun toContentUrl(page: WebpageEntity, site: WebsiteEntity): URL? {
        var url: URL? = null
        val path = toPath(page.url, site)
        storage.visit(
            path,
            object : StorageVisitor {
                override fun visit(u: URL) {
                    url = u
                }
            }
        )
        return url
    }

    private fun toPath(url: String, site: WebsiteEntity): String {
        val hash = http.hash(url)
        return "bot/sites/${site.name}/pages/$hash/content.json"
    }
}
