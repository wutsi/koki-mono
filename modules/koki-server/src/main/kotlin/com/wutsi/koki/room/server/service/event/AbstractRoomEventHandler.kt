package com.wutsi.koki.room.server.service.event

import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.StorageServiceProvider
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import kotlin.io.use

abstract class AbstractRoomEventHandler(
    private val storageServiceProvider: StorageServiceProvider,
) {
    protected fun download(file: FileEntity): File {
        val extension = FilenameUtils.getExtension(file.url)
        val f = File.createTempFile(file.name, ".$extension")
        val output = FileOutputStream(f)
        output.use {
            storageServiceProvider.get(file.tenantId).get(URI(file.url).toURL(), output)
            return f
        }
    }
}
