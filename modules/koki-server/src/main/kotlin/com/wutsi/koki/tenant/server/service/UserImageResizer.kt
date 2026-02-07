package com.wutsi.koki.tenant.server.service

import com.wutsi.koki.file.server.service.ImageResizer
import com.wutsi.koki.platform.core.image.Dimension
import com.wutsi.koki.platform.core.image.Focus
import com.wutsi.koki.platform.core.image.Format
import com.wutsi.koki.platform.core.image.ImageService
import com.wutsi.koki.platform.core.image.Transformation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class UserImageResizer(
    private val imageService: ImageService,

    @Value("\${koki.module.user.image.tiny.width}") private val tinyWidth: Int,
    @Value("\${koki.module.user.image.tiny.height}") private val tinyHeight: Int,
    @Value("\${koki.module.user.image.thumbnail.width}") private val thumbnailWidth: Int,
    @Value("\${koki.module.user.image.thumbnail.height}") private val thumbnailHeight: Int,
    @Value("\${koki.module.user.image.preview.width}") private val previewWidth: Int,
    @Value("\${koki.module.user.image.preview.height}") private val previewHeight: Int,
    @Value("\${koki.module.user.image.opengraph.width}") private val openGraphWidth: Int,
    @Value("\${koki.module.user.image.opengraph.height}") private val openGraphHeight: Int,
) : ImageResizer {
    override fun tinyUrl(url: String): String {
        return transform(url, tinyWidth, tinyHeight)
    }

    override fun thumbnailUrl(url: String): String {
        return transform(url, thumbnailWidth, thumbnailHeight)
    }

    override fun previewUrl(url: String): String {
        return transform(url, previewWidth, previewHeight)
    }

    override fun openGraphUrl(url: String): String {
        return transform(url, openGraphWidth, openGraphHeight, Format.PNG)
    }

    private fun transform(url: String, width: Int, height: Int, format: Format = Format.WEBP): String {
        return imageService.transform(
            url,
            Transformation(
                focus = Focus.AUTO,
                format = format,
                dimension = Dimension(width, height),
            )
        )
    }
}
