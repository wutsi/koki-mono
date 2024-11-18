package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.workflow.server.io.WorkflowExporter
import com.wutsi.koki.workflow.server.service.WorkflowService
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ExportWorkflowJSONEndpoint(
    private val exporter: WorkflowExporter,
    private val service: WorkflowService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExportWorkflowJSONEndpoint::class.java)
    }

    @GetMapping("/v1/workflows/json/{tenant-id}.{workflow-id}.json")
    fun png(
        @PathVariable("tenant-id") tenantId: Long,
        @PathVariable("workflow-id") id: Long,
        response: HttpServletResponse
    ) {
        response.contentType = "application/json"
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename("$tenantId.$id.json").build().toString()
        )

        try {
            val workflow = service.get(id, tenantId)
            exporter.export(workflow, response.outputStream)
        } catch (ex: NotFoundException) {
            LOGGER.warn("workflow not found", ex)
            response.status = 404
        }
    }
}
