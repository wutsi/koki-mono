package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.service.TenantIdProvider
import com.wutsi.koki.tenant.server.io.AttributeCSVExporter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping
class ExportCSVAttributeEndpoint(
    private val exporter: AttributeCSVExporter,
    private val tenantIdProvider: TenantIdProvider,
) {
    @GetMapping("/v1/attributes/csv")
    fun get(response: HttpServletResponse) {
        val tenantId = tenantIdProvider.get()

        response.setHeader("Content-Type", "text/csv")
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename("tenant_$tenantId-attributes.csv", StandardCharsets.UTF_8)
                .build()
                .toString()
        )
        exporter.export(tenantId, response.outputStream)
    }
}
