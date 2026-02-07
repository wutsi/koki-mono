package com.wutsi.koki.listing.server.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.server.service.ImageResizer
import com.wutsi.koki.file.server.service.ImageResizerProvider
import com.wutsi.koki.platform.core.image.Dimension
import com.wutsi.koki.platform.core.image.Focus
import com.wutsi.koki.platform.core.image.Format
import com.wutsi.koki.platform.core.image.ImageService
import com.wutsi.koki.platform.core.image.Transformation
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ListingImageResizer(
    private val factory: ImageResizerProvider,
    private val imageService: ImageService,

    @Value("\${koki.module.listing.image.tiny.width}") private val tinyWidth: Int,
    @Value("\${koki.module.listing.image.tiny.height}") private val tinyHeight: Int,
    @Value("\${koki.module.listing.image.thumbnail.width}") private val thumbnailWidth: Int,
    @Value("\${koki.module.listing.image.thumbnail.height}") private val thumbnailHeight: Int,
    @Value("\${koki.module.listing.image.preview.width}") private val previewWidth: Int,
    @Value("\${koki.module.listing.image.preview.height}") private val previewHeight: Int,
    @Value("\${koki.module.listing.image.opengraph.width}") private val openGraphWidth: Int,
    @Value("\${koki.module.listing.image.opengraph.height}") private val openGraphHeight: Int,
) : ImageResizer {
    @PostConstruct
    fun init() {
        factory.register(ObjectType.LISTING, this)
    }

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
