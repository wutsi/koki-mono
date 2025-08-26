package com.wutsi.koki.file.server.service.extractor

import com.wutsi.koki.file.server.service.FileInfo
import com.wutsi.koki.file.server.service.FileInfoExtractor
import org.springframework.stereotype.Service
import java.io.File
import javax.imageio.ImageIO

@Service
class ImageInfoExtractor : FileInfoExtractor {
    override fun extract(file: File): FileInfo {
        val img = ImageIO.read(file)
        return FileInfo(
            width = img.getWidth(),
            height = img.getHeight()
        )
    }
}
