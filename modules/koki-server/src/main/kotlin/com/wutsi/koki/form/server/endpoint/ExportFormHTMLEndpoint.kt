package com.wutsi.koki.form.server.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLFormGenerator
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
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
    private val generator: HTMLFormGenerator,
    private val objectMapper: ObjectMapper,
    private val activityInstanceService: ActivityInstanceService,

    @Value("\${koki.portal-url}") private val portalUrl: String,
) {
    @GetMapping("/v1/forms/html/{tenant-id}.{id}.html")
    fun html(
        @PathVariable(name = "tenant-id") tenantId: Long,
        @PathVariable id: String,
        @RequestParam(required = false, name = "aiid") activityInstanceId: String? = null,
        @RequestParam(required = false, name = "role-name") roleName: String? = null,
        response: HttpServletResponse
    ) {
        val context = createContext(id, activityInstanceId, roleName, tenantId)
        val writer = StringWriter()
        val form = service.get(id, tenantId)
        val content = objectMapper.readValue(form.content, FormContent::class.java)
        generator.generate(content, context, writer)

        response.contentType = "text/html"
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename("$tenantId.$id.html").build().toString()
        )
        IOUtils.copy(writer.toString().byteInputStream(), response.outputStream)
    }

    private fun createContext(
        id: String,
        activityInstanceId: String?,
        roleName: String?,
        tenantId: Long
    ): Context {
        val activityInstance = activityInstanceId?.let { id ->
            activityInstanceService.get(id, tenantId)
        }
        val submitUrl = StringBuilder("$portalUrl/forms/$id")
        if (activityInstanceId != null) {
            submitUrl.append("?aiid=$activityInstanceId")
        }
        return Context(
            submitUrl = submitUrl.toString(),
            roleName = roleName,
            data = activityInstance?.instance?.state?.let { state ->
                objectMapper.readValue(state, Map::class.java) as Map<String, String>
            } ?: emptyMap(),
        )
    }
}
