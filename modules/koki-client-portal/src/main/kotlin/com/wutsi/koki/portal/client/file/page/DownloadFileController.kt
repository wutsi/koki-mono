package com.wutsi.koki.portal.client.file.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.portal.client.common.page.AbstractPageController
import com.wutsi.koki.portal.client.configuration.service.ConfigurationService
import com.wutsi.koki.portal.client.file.service.FileService
import com.wutsi.koki.portal.client.security.RequiresModule
import com.wutsi.koki.portal.client.tax.service.TaxService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.UUID

@RestController
@RequestMapping("/files")
@RequiresModule(name = "file")
class DownloadFileController(
    private val service: FileService,
    private val configurationService: ConfigurationService,
    private val storageServiceBuilder: StorageServiceBuilder,
    private val taxService: TaxService
) : AbstractPageController() {
    @GetMapping("/{id}/download")
    fun download(
        @PathVariable id: Long,
        @RequestParam(name = "owner-id") ownerId: Long,
        @RequestParam(name = "owner-type") ownerType: ObjectType,
        response: HttpServletResponse
    ) {
        // File
        val file = service.files(
            ids = listOf(id),
            ownerId = ownerId,
            ownerType = ownerType,
        ).firstOrNull()
        if (file == null) {
            response.sendError(404)
            return
        }
        if (!isOwner(ownerId, ownerType)) {
            response.sendError(403)
            return
        }

        val f = File.createTempFile(UUID.randomUUID().toString(), "tmp")
        try {
            // Download the file
            val output = FileOutputStream(f)
            output.use {
                getStorageService().get(URL(file.contentUrl), output)
            }

            // Stream result
            response.contentType = file.contentType
            response.setContentLengthLong(file.contentLength)
            response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.builder("attachment")
                    .filename(file.name)
                    .build()
                    .toString()
            )

            val buff = ByteArray(1024 * 1024) // 1Mb
            val input = FileInputStream(f)
            input.use { input ->
                response.outputStream.use { output ->
                    var bytes = input.read(buff)
                    while (bytes > 0) {
                        output.write(buff, 0, bytes)
                        bytes = input.read(buff)
                    }
                }
            }
        } finally {
            f.delete()
        }
    }

    private fun getStorageService(): StorageService {
        val configs = configurationService.configurations(keyword = "storage.")
        return storageServiceBuilder.build(configs)
    }

    private fun isOwner(ownerId: Long, ownerType: ObjectType): Boolean {
        val user = userHolder.get() ?: return false

        if (ownerType == ObjectType.TAX) {
            val tax = taxService.tax(ownerId)
            return tax.account.id == user.account.id
        }
        return false
    }
}
