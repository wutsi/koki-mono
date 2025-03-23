package com.wutsi.koki.tax.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wutsi.koki.ai.server.service.AIConsumer
import com.wutsi.koki.ai.server.service.AbstractAIAgent
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.ai.genai.Document
import com.wutsi.koki.platform.ai.genai.GenAIRequest
import com.wutsi.koki.platform.ai.genai.GenAIService
import com.wutsi.koki.platform.ai.genai.GenAIServiceBuilder
import com.wutsi.koki.platform.ai.genai.GenAIType
import com.wutsi.koki.platform.ai.genai.Message
import com.wutsi.koki.platform.ai.genai.Role
import com.wutsi.koki.platform.ai.genai.gemini.GenAIConfig
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.StorageType
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL

@Service
class TaxAIAgent(
    private val fileService: FileService,
    private val storageBuilder: StorageServiceBuilder,
    private val genAIBuilder: GenAIServiceBuilder,
    private val configurationService: ConfigurationService,
    private val objectMapper: ObjectMapper,
    private val logger: KVLogger,

    registry: AIConsumer,
) : AbstractAIAgent(registry) {
    companion object {
        const val SYSTEM_INSTRUCTION = """
            You are an expert tax accountant assisting with tax preparation.
            Provide accurate and detailed information based on the latest tax laws and regulations.
            Double-check all calculations and cite relevant sources when possible.
            Return your information only in JSON format and use camel case naming convention for the field names
        """
        const val PROMPT = """
            Given the following document, can you extract the information about this document. We would like to identify:
            - The document type (use the JSON field "type"). Use the income tax supporting document codes whenever possible.
            - The fiscal year associated with this document (use the JSON field "year")
            - The name of the recipient associated with the document, if available (use the JSON field "recipientName")
            - A description of the document with a maximum of 200 words. (use the JSON field "description")
        """
    }

    override fun notify(event: Any): Boolean {
        if (event is FileUploadedEvent) {
            process(event)
            return true
        }
        return false
    }

    private fun process(event: FileUploadedEvent) {
        logger.add("file_id", event.fileId)
        logger.add("tenant_id", event.tenantId)
        logger.add("owner_id", event.owner?.id)
        logger.add("owner_type", event.owner?.type)

        if (event.owner?.type != ObjectType.TAX || isEnabled(event.tenantId)) {
            return
        }

        val file = fileService.get(event.fileId, event.tenantId)
        val f = downloadFile(file) ?: return
        try {
            val result = extractInformation(file, f)
            val labels = listOf(
                result["type"]
            ).filterNotNull()
                .map { label -> label.toString() }

            logger.add("processed", true)
            logger.add("file_label_type", result["type"])
            logger.add("file_label_year", result["year"])
            logger.add("file_description", result["description"])
            fileService.setLabels(file, labels, result["description"]?.toString())
        } finally {
            f.delete()
        }
    }

    private fun extractInformation(file: FileEntity, f: File): Map<String, Any> {
        val genAI = getGenAIService(file.tenantId) ?: return emptyMap()
        val content = FileInputStream(f)
        content.use {
            val response = genAI.generateContent(
                request = GenAIRequest(
                    messages = listOf(
                        Message(
                            role = Role.MODEL,
                            text = SYSTEM_INSTRUCTION,
                        ),
                        Message(
                            role = Role.USER,
                            text = PROMPT,
                            document = Document(
                                contentType = MediaType.valueOf(file.contentType),
                                content = content
                            )
                        ),
                    ),
                    config = GenAIConfig(
                        responseType = MediaType.APPLICATION_JSON,
                    )
                )
            )

            val json = response.messages[0].text
                ?: return emptyMap<String, Any>()

            return objectMapper.readValue<Map<String, Any>>(json)
        }
    }

    private fun downloadFile(file: FileEntity): File? {
        if (!isContentTypeSupported(file.contentType)) {
            return null
        }

        val f = File.createTempFile(file.name, "tmp")
        val output = FileOutputStream(f)
        getStorageService(file.tenantId).get(URL(file.url), output)

        return f
    }

    private fun isContentTypeSupported(contentType: String): Boolean {
        return contentType.startsWith("text/") ||
            contentType.startsWith("image/") ||
            contentType == "application/pdf"
    }

    private fun isEnabled(tenantId: Long): Boolean {
        val configs = configurationService.search(
            tenantId = tenantId,
            names = listOf(
                ConfigurationName.AI_MODEL,
                ConfigurationName.TAX_AI_AGENT_ENABLED,
            )
        )
        return configs.size == 2
    }

    private fun getStorageService(tenantId: Long): StorageService {
        val configs = configurationService.search(keyword = "storage.", tenantId = tenantId)
            .map { config -> config.name to config.value }
            .toMap()

        val type = configs[ConfigurationName.STORAGE_TYPE]?.let { type -> StorageType.valueOf(type) }
            ?: StorageType.KOKI
        return storageBuilder.build(type, configs)
    }

    private fun getGenAIService(tenantId: Long): GenAIService? {
        val configs = configurationService.search(keyword = "ai.", tenantId = tenantId)
            .map { config -> config.name to config.value }
            .toMap()

        val type = configs[ConfigurationName.AI_MODEL]?.let { type -> GenAIType.valueOf(type) }
            ?: return null
        return genAIBuilder.build(type, configs)
    }
}
