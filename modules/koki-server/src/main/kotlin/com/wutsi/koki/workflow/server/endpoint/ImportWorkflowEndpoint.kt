package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.workflow.dto.ImportJSONWorkflowRequest
import com.wutsi.koki.workflow.server.io.WorkflowJSONImporter
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ImportWorkflowJSONEndpoint(
    private val importer: WorkflowJSONImporter,
) {
    @PostMapping("/v1/workflow/json")
    fun import(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestBody @Valid json: ImportJSONWorkflowRequest
    ): ImportWorkflowJSONEndpoint {
        importer.import(tenantId, file.inputStream)
    }
}
