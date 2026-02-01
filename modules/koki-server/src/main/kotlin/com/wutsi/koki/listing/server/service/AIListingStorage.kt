package com.wutsi.koki.listing.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.server.domain.AIListingEntity
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ai.ListingContentParserResult
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageVisitor
import com.wutsi.koki.tenant.server.service.StorageProvider
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL

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
        val path = "tenant/${listing.tenantId}/listing/${listing.id}/ai"
        val storage = storageProvider.get(listing.tenantId)
        store("$path/request.json", request, storage)
        store("$path/result.json", result, storage)
    }

    fun get(listing: ListingEntity): AIListingEntity {
        val tenantId = listing.tenantId
        val listingId = listing.id ?: -1
        val storage = storageProvider.get(tenantId)
        return AIListingEntity(
            id = -1,
            listing = listing,
            tenantId = tenantId,
            text = readContent("tenant/$tenantId/listing/$listingId/ai/request.json", storage),
            result = readContent("tenant/$tenantId/listing/$listingId/ai/result.json", storage),
        )
    }

    private fun store(path: String, obj: Any, storage: StorageService) {
        try {
            val content = jsonMapper.writeValueAsString(obj).toByteArray(Charsets.UTF_8)
            storage.store(path, ByteArrayInputStream(content), "application/json", content.size.toLong())
        } catch (ex: Exception) {
            LOGGER.warn("Unable to store object at $path", ex)
        }
    }

    private fun readContent(path: String, storage: StorageService): String {
        // Get the URL
        var u: URL? = null
        val visitor = object : StorageVisitor {
            override fun visit(url: URL) {
                u = url
            }
        }
        storage.visit(path, visitor)

        // Extract the content
        if (u != null) {
            val output = ByteArrayOutputStream()
            storage.get(u, output)
            return output.toString("UTF-8")
        } else {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.LISTING_AI_NOT_FOUND,
                    message = "Content not found: $path"
                )
            )
        }
    }
}
