package com.wutsi.koki.portal.file.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.portal.account.service.AccountService
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.contact.service.ContactService
import com.wutsi.koki.portal.file.service.FileService
import com.wutsi.koki.portal.product.service.ProductService
import com.wutsi.koki.portal.room.service.RoomService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    private val accountService: AccountService,
    private val contactService: ContactService,
    private val roomService: RoomService,
    private val productService: ProductService,
) : AbstractPageController() {
    @GetMapping("/files/{id}")
    fun show(
        @PathVariable id: Long,
        @RequestParam("owner-id") ownerId: Long,
        @RequestParam("owner-type") ownerType: ObjectType,
        @RequestParam("read-only", required = false) readOnly: Boolean = false,
        model: Model
    ): String {
        val file = service.file(id)
        model.addAttribute("file", file)

        model.addAttribute("ownerType", ownerType)
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("readOnly", readOnly)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FILE,
                title = file.name
            )
        )

        val ownerModule = tenantHolder.get()!!.modules.find { module -> module.objectType == ownerType }!!
        model.addAttribute("ownerModule", ownerModule)
        model.addAttribute("ownerUrl", "${ownerModule.homeUrl}/$ownerId?tab=file")
        model.addAttribute(
            "ownerName",
            when (ownerType) {
                ObjectType.ACCOUNT -> accountService.account(ownerId, fullGraph = false).name
                ObjectType.CONTACT -> contactService.contact(ownerId, fullGraph = false).name
                ObjectType.PRODUCT -> productService.product(ownerId, fullGraph = false).name
                ObjectType.ROOM -> roomService.room(ownerId, fullGraph = false).title
                else -> null
            }
        )
        return "files/show"
    }

    @GetMapping("/files/{id}/delete")
    @RequiresPermission(["file:delete"])
    fun delete(
        @PathVariable id: Long,
        @RequestParam("owner-id") ownerId: Long,
        @RequestParam("owner-type") ownerType: ObjectType,
    ): String {
        val file = service.file(id)
        service.delete(id)

        val module = tenantHolder.get()!!.modules.find { module -> module.objectType == ownerType }!!
        val tab = if (file.type == FileType.IMAGE) {
            "image"
        } else {
            "file"
        }
        return "redirect:${module.homeUrl}/$ownerId?tab=$tab"
    }

    @GetMapping("/files/{id}/download")
    fun download(
        @PathVariable id: Long,
        response: HttpServletResponse
    ) {
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
        return storageServiceBuilder.build(configs)
    }
}
