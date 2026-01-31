package com.wutsi.koki.listing.server.service

import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ai.ListingContentParserResult
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.tenant.server.service.StorageProvider
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper
import java.io.ByteArrayInputStream

@Service
class AIListingStorage(
    private val storageProvider: StorageProvider,
    private val jsonMapper: JsonMapper,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AIListingStorage::class.java)
    }

    @Async
    fun store(
        request: CreateAIListingRequest,
        result: ListingContentParserResult,
        listing: ListingEntity,
    ) {
        val path = "tenant/${listing.tenantId}/listing/${listing.id}/__ai"
        val storage = storageProvider.get(listing.tenantId)
        store("$path/request.json", request, storage)
        store("$path/result.json", result, storage)
    }

    private fun store(path: String, obj: Any, storage: StorageService) {
        try {
            val content = jsonMapper.writeValueAsString(obj).toByteArray(Charsets.UTF_8)
            storage.store(path, ByteArrayInputStream(content), "application/json", content.size.toLong())
        } catch (ex: Exception) {
            LOGGER.warn("Unable to store object at $path", ex)
        }
    }
}
