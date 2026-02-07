package com.wutsi.koki.file.server.service

import com.wutsi.koki.common.dto.ObjectType
import org.springframework.stereotype.Service

@Service
class ImageResizerFactory {
    private val resizers: MutableMap<ObjectType, ImageResizer> = mutableMapOf()

    fun register(objectType: ObjectType, transformer: ImageResizer) {
        resizers[objectType] = transformer
    }

    fun get(objectType: ObjectType): ImageResizer? =
        resizers[objectType]
}
