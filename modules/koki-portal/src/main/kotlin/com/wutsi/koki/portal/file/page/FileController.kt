package com.wutsi.koki.portal.file.page

import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.StorageType
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.UUID

@Controller
@RequestMapping
@RequiresPermission(["file"])
class FileController(
    private val service: FileService,
    private val configurationService: ConfigurationService,
    private val storageServiceBuilder: StorageServiceBuilder,
) : AbstractPageController() {
    @GetMapping("/files/{id}/delete")
    @RequiresPermission(["file:delete"])
    fun delete(@PathVariable id: Long): String {
        service.delete(id)
        return "files/deleted"
    }

    @GetMapping("/files/{id}/download")
    fun download(@PathVariable id: Long, response: HttpServletResponse) {
        // File
        val file = service.file(id)

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
        val type = try {
            configs[ConfigurationName.STORAGE_TYPE]?.let { type -> StorageType.valueOf(type.uppercase()) }
        } catch (ex: Exception) {
            null
        }

        return storageServiceBuilder.build((type ?: StorageType.KOKI), configs)
    }
}
