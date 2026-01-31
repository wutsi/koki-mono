package com.wutsi.koki.bot.server.dao

import com.wutsi.koki.bot.server.domain.WebsiteEntity
import com.wutsi.koki.bot.server.domain.WebsiteListEntity
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper

@Service
class WebsiteRepository(private val jsonMapper: JsonMapper) {
    private var list: WebsiteListEntity

    init {
        list = jsonMapper.readValue(
            WebsiteRepository::class.java.getResourceAsStream("/websites.json"),
            WebsiteListEntity::class.java
        )
    }

    fun findAll(): List<WebsiteEntity> {
        return list.websites
    }

    fun findByName(name: String): WebsiteEntity? {
        return list.websites.firstOrNull { it.name == name }
    }
}
