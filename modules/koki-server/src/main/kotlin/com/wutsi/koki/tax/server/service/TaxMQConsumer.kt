package com.wutsi.koki.tax.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.note.dto.event.NoteCreatedEvent
import com.wutsi.koki.note.dto.event.NoteDeletedEvent
import com.wutsi.koki.note.dto.event.NoteUpdatedEvent
import com.wutsi.koki.note.server.service.NoteOwnerService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.tax.dto.TaxFileData
import com.wutsi.koki.tax.server.service.ai.TaxAgentFactory
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Service
class TaxMQConsumer(
    private val taxService: TaxService,
    private val noteOwnerService: NoteOwnerService,
    private val configurationService: ConfigurationService,
    private val fileService: FileService,
    private val accountService: AccountService,
    private val taxFileService: TaxFileService,
    private val storageServiceProvider: StorageServiceProvider,
    private val objectMapper: ObjectMapper,
    private val taxAgentFactory: TaxAgentFactory,
    private val logger: KVLogger,
) : Consumer {
    companion object {
        const val TAX_FILE_AGENT_QUERY =
            "This document has been provided by my client for the preparation of his taxes. " +
                "I want to verify is this document is needed for this preparation of his taxes. " +
                "Can you help me to understand what type of document it is? "
    }

    override fun consume(event: Any): Boolean {
        if (event is NoteCreatedEvent) {
            onNoteCreated(event)
        } else if (event is NoteUpdatedEvent) {
            onNoteUpdated(event)
        } else if (event is NoteDeletedEvent) {
            onNoteDeleted(event)
        } else if (event is FileUploadedEvent) {
            onFileUploaded(event)
        } else {
            return false
        }
        return true
    }

    private fun onNoteCreated(event: NoteCreatedEvent) {
        onNoteEvent(event.noteId, event.tenantId)
    }

    private fun onNoteUpdated(event: NoteUpdatedEvent) {
        onNoteEvent(event.noteId, event.tenantId)
    }

    private fun onNoteDeleted(event: NoteDeletedEvent) {
        onNoteEvent(event.noteId, event.tenantId)
    }

    private fun onNoteEvent(noteId: Long, tenantId: Long) {
        logger.add("event_note_id", noteId)
        logger.add("event_tenant_id", tenantId)

        val owners = noteOwnerService.findByNoteIdAndOwnerType(noteId, ObjectType.TAX)
        owners.forEach { owner ->
            logger.add("tax_id", owner.ownerId)
            taxService.updateMetrics(owner.ownerId, tenantId)
        }
    }

    private fun onFileUploaded(event: FileUploadedEvent) {
        logger.add("file_id", event.fileId)
        logger.add("tenant_id", event.tenantId)
        logger.add("owner_id", event.owner?.id)
        logger.add("owner_type", event.owner?.type)

        if (event.owner?.type != ObjectType.TAX || !isAIAgentEnabled(event.tenantId)) {
            return
        }

        val file = fileService.get(event.fileId, event.tenantId)
        val tax = taxService.get(event.owner!!.id, event.tenantId)
        val account = accountService.get(tax.accountId, event.tenantId)
        val f = download(file) ?: return
        try {
            val data = extract(account, f)
            taxFileService.save(file, data)
        } finally {
            f.delete()
        }
    }

    private fun extract(account: AccountEntity, f: File): TaxFileData {
        val agent = taxAgentFactory.createTaxFileAgent(account)
        val data = agent.run(TAX_FILE_AGENT_QUERY, f)
        return objectMapper.readValue(data, TaxFileData::class.java)
    }

    private fun download(file: FileEntity): File? {
        if (!isContentTypeSupported(file.contentType)) {
            return null
        }
        val extension = FilenameUtils.getExtension(file.url)
        val f = File.createTempFile(file.name, ".$extension")
        val output = FileOutputStream(f)
        output.use {
            storageServiceProvider.get(file.tenantId)
                .get(URL(file.url), output)
            return f
        }
    }

    private fun isContentTypeSupported(contentType: String): Boolean {
        return contentType.startsWith("text/") ||
            contentType.startsWith("image/") ||
            contentType == "application/pdf"
    }

    private fun isAIAgentEnabled(tenantId: Long): Boolean {
        val configs = configurationService.search(
            tenantId = tenantId,
            names = listOf(
                ConfigurationName.AI_PROVIDER,
                ConfigurationName.TAX_AI_AGENT_ENABLED,
            )
        )
        return configs.size >= 2
    }
}
