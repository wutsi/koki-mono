package com.wutsi.koki.form.server.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.FileResolver
import com.wutsi.koki.form.server.generator.html.HTMLFormGenerator
import com.wutsi.koki.form.server.service.FormDataService
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.security.server.service.SecurityService
import com.wutsi.koki.tenant.server.service.UserService
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.StringWriter

@RestController
@RequestMapping
class ExportFormHTMLEndpoint(
    private val service: FormService,
    private val formDataService: FormDataService,
    private val generator: HTMLFormGenerator,
    private val objectMapper: ObjectMapper,
    private val securityService: SecurityService,
    private val userService: UserService,
    private val fileResolver: FileResolver,

    @Value("\${koki.portal-url}") private val portalUrl: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExportFormHTMLEndpoint::class.java)
    }

    @GetMapping("/v1/forms/html/{tenant-id}/{form-id}.html")
    fun formHtml(
        @PathVariable(name = "tenant-id") tenantId: Long,
        @PathVariable(name = "form-id") formId: String,
        @RequestParam(required = false, name = "workflow-instance-id") workflowInstanceId: String? = null,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean = false,
        response: HttpServletResponse
    ) {
        generateHtml(tenantId, formId, null, workflowInstanceId, activityInstanceId, readOnly, response)
    }

    @GetMapping("/v1/forms/html/{tenant-id}/{form-id}/{form-data-id}.html")
    fun formDataHtml(
        @PathVariable(name = "tenant-id") tenantId: Long,
        @PathVariable(name = "form-id") formId: String,
        @PathVariable(name = "form-data-id") formDataId: String?,
        @RequestParam(required = false, name = "activity-instance-id") activityInstanceId: String? = null,
        @RequestParam(required = false, name = "read-only") readOnly: Boolean = false,
        response: HttpServletResponse
    ) {
        generateHtml(tenantId, formId, formDataId, null, activityInstanceId, readOnly, response)
    }

    private fun generateHtml(
        tenantId: Long,
        formId: String,
        formDataId: String?,
        workflowInstanceId: String?,
        activityInstanceId: String?,
        readOnly: Boolean,
        response: HttpServletResponse
    ) {
        response.contentType = "text/html"
        try {
            val form = service.get(formId, tenantId)
            val context = createContext(form, formDataId, workflowInstanceId, activityInstanceId, readOnly, tenantId)
            val writer = StringWriter()
            val content = objectMapper.readValue(form.content, FormContent::class.java)
            generator.generate(content, context, writer)

            val filename = formDataId ?: formId
            response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename("$filename.html").build().toString()
            )
            IOUtils.copy(writer.toString().byteInputStream(), response.outputStream)
        } catch (ex: NotFoundException) {
            LOGGER.warn("Unable to generate html", ex)
            response.status = 404
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun createContext(
        form: FormEntity,
        formDataId: String?,
        workflowInstanceId: String?,
        activityInstanceId: String?,
        readOnly: Boolean,
        tenantId: Long,
    ): Context {
        val data = if (formDataId != null) {
            formDataService.get(formDataId, tenantId).data
        } else if (workflowInstanceId != null) {
            formDataService.search(
                tenantId = tenantId,
                workflowInstanceIds = listOf(workflowInstanceId)
            ).firstOrNull()?.data
        } else {
            null
        }

        return Context(
            tenantId = tenantId,
            roleNames = getRoleNames(tenantId),
            readOnly = readOnly,
            fileResolver = fileResolver,
            data = data?.let { data ->
                objectMapper.readValue(data, Map::class.java) as Map<String, Any>
            } ?: emptyMap(),
            submitUrl = buildSubmitUrl(
                form = form,
                formDataId = formDataId,
                workflowInstanceId = workflowInstanceId,
                activityInstanceId = activityInstanceId
            ),
            uploadUrl = buildUploadUrl(
                form = form,
                workflowInstanceId = workflowInstanceId,
            ),
            downloadUrl = getStorageUrl()
        )
    }

    private fun buildSubmitUrl(
        form: FormEntity,
        formDataId: String?,
        workflowInstanceId: String?,
        activityInstanceId: String?,
    ): String {
        val url = StringBuilder("$portalUrl/forms/${form.id}")
        formDataId?.let { url.append("/$formDataId") }
        workflowInstanceId?.let { url.append("?workflow-instance-id=$workflowInstanceId") }
        activityInstanceId?.let {
            if (workflowInstanceId != null) {
                url.append("&")
            } else {
                url.append("?")
            }
            url.append("activity-instance-id=$activityInstanceId")
        }
        return url.toString()
    }

    private fun buildUploadUrl(
        form: FormEntity,
        workflowInstanceId: String?,
    ): String {
        val url = StringBuilder(getStorageUrl() + "?form-id=${form.id}")
        workflowInstanceId?.let { url.append("&workflow-instance-id=$workflowInstanceId") }
        return url.toString()
    }

    private fun getStorageUrl(): String {
        return "$portalUrl/storage"
    }

    private fun getRoleNames(tenantId: Long): List<String> {
        val userId = securityService.getCurrentUserIdOrNull()
        if (userId == null) {
            return emptyList()
        }

        val user = userService.get(userId, tenantId)
        return user.roles.map { role -> role.name }
    }
}
