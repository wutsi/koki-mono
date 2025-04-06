package com.wutsi.koki.tax.server.service.ai

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.ai.server.service.AIMQConsumer
import com.wutsi.koki.ai.server.service.AbstractAIAgent
import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.platform.ai.agent.DefaultAgent
import com.wutsi.koki.platform.ai.llm.Document
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tax.server.service.TaxService
import com.wutsi.koki.tax.server.service.ai.tools.TaxFormTool
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Locale

/**
 * Identify the Tax forms.
 * Here are the sources:
 *  - Canada: https://www.canada.ca/en/revenue-agency/services/forms-publications/forms.html
 *  - Quebec: https://www.revenuquebec.ca/fr/services-en-ligne/formulaires-et-publications/citoyens/
 */
@Service
class FormIdentifierAgent(
    private val fileService: FileService,
    private val taxService: TaxService,
    private val accountService: AccountService,
    private val formService: FormService,
    private val storageServiceProvider: StorageServiceProvider,
    private val llmProvider: LLMProvider,
    private val configurationService: ConfigurationService,
    private val logger: KVLogger,

    registry: AIMQConsumer,
) : AbstractAIAgent(registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(FormIdentifierAgent::class.java)

        const val EXPENSE_CODE = "EXP"

        const val SYSTEM_INSTRUCTIONS = """
            You are an expert tax accountant assisting with tax preparation.
            Provide accurate and detailed information based on the latest tax laws and regulations.
            Return the result in JSON format.
        """
        const val PROMPT = """
            Can you analyse the provided document submitted by a user from {{country}} for whom we want to prepare his taxes.
            Return the result in the following JSON format:
            {
               "code": "Code of the document. Example: T1",
               "description": "Short summary of the document content in 255 characters or less"
            }

            The type of form you will identify are the following:
            - Official forms from the Revenue Agency of the country.
            - Internal forms.
            - Common expenses (medical expense, charitable or political donation, exam for professional certification, etc.). Use the code "$EXPENSE_CODE"

            List of Internal forms (in the format CODE: DESCRIPTION):
            {{internal_forms}}
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

        if (event.owner?.type != ObjectType.TAX || !isEnabled(event.tenantId)) {
            return
        }

        val file = fileService.get(event.fileId, event.tenantId)
        val tax = taxService.get(event.owner!!.id, event.tenantId)
        val account = accountService.get(tax.accountId, event.tenantId)
        val f = download(file) ?: return
        try {
            val data = extract(file, account, f)
            if (data.isNotEmpty()) {
                logger.add("file_code", data["code"])

                fileService.setLabels(
                    file = file,
                    labels = data["code"]?.let { code -> listOf(code.toString()) } ?: emptyList(),
                    description = data["description"].toString(),
                )
            }
        } finally {
            f.delete()
        }
    }

    private fun extract(file: FileEntity, account: AccountEntity, f: File): Map<String, Any> {
        val output = ByteArrayOutputStream()
        output.use {
            val prompt = buildPrompt(account)
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug("Prompt: $prompt")
            }

            DefaultAgent(
                llm = llmProvider.get(file.tenantId),
                tools = listOf(TaxFormTool()),
                responseType = MediaType.APPLICATION_JSON,
                systemInstructions = SYSTEM_INSTRUCTIONS,
            ).run(
                query = prompt,
                file = Document(
                    contentType = MediaType.valueOf(file.contentType),
                    content = ByteArrayInputStream(f.readBytes())
                ),
                output = output,
            )
            return ObjectMapper().readValue(output.toByteArray(), Any::class.java) as Map<String, Any>
        }
    }

    private fun buildPrompt(account: AccountEntity): String {
        val country = account.shippingCountry ?: ""
        val forms = formService.search(
            tenantId = account.tenantId,
            active = true,
            limit = Integer.MAX_VALUE,
        )
        return PROMPT
            .replace("{{country}}", Locale("en", country).displayCountry)
            .replace(
                "{{internal_forms}}",
                if (forms.isEmpty()) {
                    "No internal form!"
                } else {
                    forms.map { form -> "- ${form.code}: ${form.name}" }.joinToString("\n")
                }
            )
    }

    private fun download(file: FileEntity): File? {
        if (!isContentTypeSupported(file.contentType)) {
            return null
        }
        val f = File.createTempFile(file.name, "tmp")
        val output = FileOutputStream(f)
        storageServiceProvider.get(file.tenantId).get(URL(file.url), output)
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
                ConfigurationName.AI_PROVIDER,
                ConfigurationName.TAX_AI_AGENT_ENABLED,
            )
        )
        return configs.size >= 2
    }
}
